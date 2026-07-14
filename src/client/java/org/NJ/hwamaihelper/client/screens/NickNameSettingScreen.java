package org.NJ.hwamaihelper.client.screens;

import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiColorEditorHSV;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.NJ.hwamaihelper.client.components.ActionButtonBar;
import org.NJ.hwamaihelper.client.components.NickEditorPanel;
import org.NJ.hwamaihelper.client.components.NickPreviewHeader;
import org.NJ.hwamaihelper.client.components.NickSectionWidget;
import org.NJ.hwamaihelper.client.components.SaveSlotPanel;
import org.NJ.hwamaihelper.client.logic.NickNameManager;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Nickname editor hosted by MaLiLib, including MaLiLib's HSV color dialog. */
public class NickNameSettingScreen extends GuiBase {
    private final Minecraft client = Minecraft.getInstance();
    private final NickNameManager manager = new NickNameManager();

    private final NickPreviewHeader header = new NickPreviewHeader();
    private final NickEditorPanel editor = new NickEditorPanel();
    private final ActionButtonBar actions = new ActionButtonBar();
    private final SaveSlotPanel saveSlots = new SaveSlotPanel();

    private double scrollAmount;

    public NickNameSettingScreen() {
        setTitle("暱稱編輯器");
    }

    @Override
    public void initGui() {
        super.initGui();

        if (manager.sections.isEmpty()) {
            loadDefaultFromConfig();
        }

        addButton(new ButtonGeneric(10, 8, 90, 20, "返回設定"),
                (button, mouseButton) -> closeGui(true));
        addButton(new ButtonGeneric(10, 32, 90, 20, "自訂指令"),
                (button, mouseButton) -> GuiBase.openGui(new CommandListScreen().setParent(getParent())));

        refreshWidgets();
    }

    private void loadDefaultFromConfig() {
        NJConfig config = NJConfigManager.getInstance();
        if (config != null && config.nickSections != null && !config.nickSections.isEmpty()) {
            manager.sections.clear();
            manager.sections.addAll(deepCopySections(config.nickSections));
        } else {
            manager.sections.add(new NickSection("暱稱", "#FFFFFF", ""));
        }
    }

    private void refreshWidgets() {
        int centerX = (width / 2) + 15;
        int buttonY = (int) (60 + (manager.sections.size() * 24) - scrollAmount);

        editor.refresh(manager, centerX, scrollAmount, this::refreshWidgets);

        actions.init(centerX - 22, buttonY, new ActionButtonBar.ActionCallbacks() {
            @Override
            public void onAdd() {
                editor.syncToManager(manager);
                manager.addSection();
                refreshWidgets();
            }

            @Override
            public void onApply() {
                editor.syncToManager(manager);
                if (client.player != null) {
                    client.player.connection.sendCommand("chmc 設定 自己 暱稱 " + manager.buildCommand());
                }
            }

            @Override
            public void onCancel() {
                if (client.player != null) {
                    client.player.connection.sendCommand("chmc 設定 自己 取消暱稱");
                }
            }

            @Override
            public void onSave() {
                saveToNewSlot();
                refreshWidgets();
            }
        });

        saveSlots.refresh(Math.max(centerX + 120, width - 85), new SaveSlotPanel.SaveSlotCallbacks() {
            @Override
            public void onApplySave(List<NickSection> sections) {
                manager.sections.clear();
                manager.sections.addAll(deepCopySections(sections));
                refreshWidgets();
            }

            @Override
            public void onRefreshRequest() {
                refreshWidgets();
            }
        });
    }

    private void openColorEditor(int sectionIndex, int colorTarget) {
        editor.syncToManager(manager);
        if (sectionIndex < 0 || sectionIndex >= manager.sections.size()) {
            return;
        }

        NickSection section = manager.sections.get(sectionIndex);
        String currentColor = colorTarget == 2
                ? (section.has("shadow") ? section.shadowColor : section.color2)
                : section.color;

        ConfigColor color = new ConfigColor("nicknameColor", toMalilibColor(currentColor),
                "編輯這一段暱稱的顏色", "暱稱顏色");
        color.setValueChangeCallback(config -> {
            String rgb = toNicknameColor(config.getStringValue());
            NickSectionWidget widget = sectionIndex < editor.sectionWidgets.size()
                    ? editor.sectionWidgets.get(sectionIndex)
                    : null;

            if (colorTarget == 2) {
                if (section.has("shadow")) {
                    section.shadowColor = rgb;
                    if (widget != null) widget.shadowColor = rgb;
                } else {
                    section.color2 = rgb;
                    if (widget != null) widget.color2 = rgb;
                }
            } else {
                section.color = rgb;
                if (widget != null) widget.color = rgb;
            }
            save();
        });

        GuiBase.openGui(new GuiColorEditorHSV(color, null, this));
    }

    private static String toMalilibColor(String value) {
        String rgb = toNicknameColor(value).substring(1);
        return "#FF" + rgb;
    }

    private static String toNicknameColor(String value) {
        String clean = value == null ? "" : value.replace("#", "").trim();
        if (clean.length() >= 8) {
            clean = clean.substring(clean.length() - 6);
        }
        if (clean.length() != 6) {
            clean = "FFFFFF";
        }
        return "#" + clean.toUpperCase(Locale.ROOT);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        editor.syncToManager(manager);
        int centerX = (width / 2) + 15;

        header.extractRenderState(context, width, manager, saveSlots.getHoveredSections());

        context.enableScissor(0, 55, width, height);
        editor.extractRenderState(context, mouseX, mouseY, delta, height);
        actions.extractRenderState(context, mouseX, mouseY, delta);
        saveSlots.extractRenderState(context, mouseX, mouseY, delta,
                Math.max(centerX + 120, width - 85), width, height);
        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();

        for (int i = 0; i < editor.sectionWidgets.size(); i++) {
            NickSectionWidget widget = editor.sectionWidgets.get(i);
            NickSection section = manager.sections.get(i);

            if (widget.mouseClicked(click, doubled)) {
                for (NickSectionWidget other : editor.sectionWidgets) {
                    if (other != widget) other.textField.setFocused(false);
                }
                return true;
            }

            int rectX = widget.textField.getX() - 22;
            int rectY = widget.textField.getY();
            if (mouseY >= rectY && mouseY <= rectY + 20
                    && mouseX >= rectX && mouseX <= rectX + 20) {
                int colorTarget = (section.has("gradient") || section.has("shadow"))
                        && mouseX >= rectX + 10 ? 2 : 1;
                for (NickSectionWidget other : editor.sectionWidgets) {
                    other.textField.setFocused(false);
                }
                openColorEditor(i, colorTarget);
                return true;
            }
        }

        return saveSlots.mouseClicked(click, doubled) || actions.mouseClicked(click, doubled);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        for (NickSectionWidget widget : editor.sectionWidgets) {
            if (widget.textField.isFocused()) {
                return widget.textField.charTyped(input);
            }
        }
        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == 256) {
            save();
            closeGui(true);
            return true;
        }

        for (NickSectionWidget widget : editor.sectionWidgets) {
            if (widget.textField.isFocused()) {
                return widget.textField.keyPressed(input);
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontalAmount, double verticalAmount) {
        scrollAmount = Math.max(0, scrollAmount - (verticalAmount * 15));
        refreshWidgets();
        return true;
    }

    public void save() {
        editor.syncToManager(manager);
        NJConfig config = NJConfigManager.getInstance();
        if (config != null) {
            config.nickSections = new ArrayList<>(manager.sections);
            NJConfigManager.save();
        }
    }

    @Override
    public void removed() {
        save();
        super.removed();
    }

    private List<NickSection> deepCopySections(List<NickSection> original) {
        return original.stream().map(NickSection::copy).collect(Collectors.toList());
    }

    private void saveToNewSlot() {
        editor.syncToManager(manager);
        NJConfig config = NJConfigManager.getInstance();
        if (config != null) {
            if (config.savedNicknames == null) config.savedNicknames = new ArrayList<>();
            config.savedNicknames.add(new NJConfig.NickGroup("新儲存", deepCopySections(manager.sections)));
            NJConfigManager.save();
        }
    }
}
