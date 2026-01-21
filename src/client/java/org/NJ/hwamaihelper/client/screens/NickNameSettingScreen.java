package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.NJ.hwamaihelper.client.components.*;
import org.NJ.hwamaihelper.client.logic.NickNameManager;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NickNameSettingScreen implements NJTab {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final NickNameManager manager = new NickNameManager();

    private final NickPreviewHeader header = new NickPreviewHeader();
    private final NickEditorPanel editor = new NickEditorPanel();
    private final ActionButtonBar actions = new ActionButtonBar();
    private final SaveSlotPanel saveSlots = new SaveSlotPanel();

    private ColorPickerComponent picker;
    private int width, height;
    private double scrollAmount = 0;
    private boolean showPicker = false;

    private int activeColorTarget = 1;

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.picker = new ColorPickerComponent(0, 0, 70);
        if (manager.sections.isEmpty()) loadDefaultFromConfig();
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
        int btnY = (int) (60 + (manager.sections.size() * 24) - scrollAmount);

        editor.refresh(manager, centerX, scrollAmount, this::refreshWidgets, (index, x, y) -> {
            manager.activeColorIndex = index;
            showPicker = true;
            picker.x = x + 30;
            picker.y = y - 10;
        });

        int totalAlignX = centerX - 22;

        actions.init(totalAlignX, btnY, new ActionButtonBar.ActionCallbacks() {
            @Override
            public void onAdd() {
                editor.syncToManager(manager);
                manager.addSection();
                refreshWidgets();
            }

            @Override
            public void onApply() {
                editor.syncToManager(manager);
                client.player.networkHandler.sendChatCommand("chmc 設定 自己 暱稱 " + manager.buildCommand());
            }

            @Override
            public void onCancel() {
                client.player.networkHandler.sendChatCommand("chmc 設定 自己 取消暱稱");
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        editor.syncToManager(manager);
        int centerX = (width / 2) + 15;

        header.render(context, width, manager, saveSlots.getHoveredSections());

        context.enableScissor(0, 55, width, height); // 放寬剪裁範圍確保渲染
        editor.render(context, mouseX, mouseY, delta, this.height);
        actions.render(context, mouseX, mouseY, delta);
        saveSlots.render(context, mouseX, mouseY, delta, Math.max(centerX + 120, width - 85), width, height);
        context.disableScissor();

        if (showPicker && manager.activeColorIndex < manager.sections.size()) {
            NickSection s = manager.sections.get(manager.activeColorIndex);
            String displayHex = (activeColorTarget == 2) ?
                    (s.has("shadow") ? s.shadowColor : s.color2) : s.color;
            picker.render(context, displayHex);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean d) {
        double mx = click.x();
        double my = click.y();

        if (showPicker && (picker.isClickInHue(mx, my) || picker.isClickInBox(mx, my))) {
            updateColor(mx, my);
            return true;
        }

        // 點擊 UI 其他部分時的焦點處理
        boolean handled = false;
        for (int i = 0; i < editor.sectionWidgets.size(); i++) {
            NickSectionWidget w = editor.sectionWidgets.get(i);
            NickSection s = manager.sections.get(i);

            // 1. 檢查 Widget 內部點擊 (輸入框與按鈕)
            if (w.mouseClicked(click, d)) {
                // 如果點的是這一段，其他段落取消焦點
                for (NickSectionWidget other : editor.sectionWidgets) {
                    if (other != w) other.textField.setFocused(false);
                }
                handled = true;
                break;
            }

            // 2. 檢查色塊點擊 (調色盤觸發)
            int rectX = w.textField.getX() - 22;
            int rectY = w.textField.getY();
            if (my >= rectY && my <= rectY + 20 && mx >= rectX && mx <= rectX + 20) {
                manager.activeColorIndex = i;
                showPicker = true;
                picker.x = (int) mx + 10;
                picker.y = (int) my - 10;

                // 判斷點左邊還是右邊
                if ((s.has("gradient") || s.has("shadow")) && mx >= rectX + 10) {
                    activeColorTarget = 2;
                } else {
                    activeColorTarget = 1;
                }

                // 取消所有輸入框焦點
                for (NickSectionWidget all : editor.sectionWidgets) all.textField.setFocused(false);
                return true;
            }
        }

        if (handled) {
            showPicker = false;
            return true;
        }

        if (saveSlots.mouseClicked(click, d) || actions.mouseClicked(click, d)) {
            showPicker = false;
            return true;
        }

        showPicker = false;
        return false;
    }

    /**
     * 修正：鍵盤輸入轉發
     * 必須從 CharInput 提取原始 char 與 modifiers
     */
    @Override
    public boolean charTyped(CharInput i) {
        for (NickSectionWidget w : editor.sectionWidgets) {
            if (w.textField.isFocused()) {
                // 直接傳入 CharInput 物件，讓 TextFieldWidget 自己處理
                return w.textField.charTyped(i);
            }
        }
        return false;
    }

    /**
     * 修正：按鍵轉發
     * 同樣需要從 KeyInput 提取原始資料
     */
    @Override
    public boolean keyPressed(KeyInput i) {
        // 處理 ESC 鍵關閉視窗 (256 是 GLFW_KEY_ESCAPE)
        if (i.key() == 256) {
            if (showPicker) {
                showPicker = false;
                return true;
            }
            this.save();
            client.setScreen(null);
            return true;
        }

        for (NickSectionWidget w : editor.sectionWidgets) {
            if (w.textField.isFocused()) {
                // 修正點：直接傳入整個 KeyInput 物件 i
                return w.textField.keyPressed(i);
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyInput i) {
        return false;
    }

    private void updateColor(double mx, double my) {
        if (manager.activeColorIndex >= manager.sections.size()) return;

        NickSection s = manager.sections.get(manager.activeColorIndex);
        NickSectionWidget widget = editor.sectionWidgets.get(manager.activeColorIndex);

        String curHex = (activeColorTarget == 2) ?
                (s.has("shadow") ? s.shadowColor : s.color2) : s.color;

        if (curHex == null) curHex = "#FFFFFF";

        String hex = picker.pickColor(mx, my, curHex);

        if (activeColorTarget == 2) {
            if (s.has("shadow")) {
                s.shadowColor = hex;
                widget.shadowColor = hex;
            } else {
                s.color2 = hex;
                widget.color2 = hex;
            }
        } else {
            s.color = hex;
            widget.color = hex;
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double h, double v) {
        this.scrollAmount = Math.max(0, this.scrollAmount - (v * 15));
        refreshWidgets();
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double x, double y) {
        if (showPicker && (picker.isClickInHue(click.x(), click.y()) || picker.isClickInBox(click.x(), click.y()))) {
            updateColor(click.x(), click.y());
            return true;
        }
        return false;
    }

    @Override
    public void save() {
        editor.syncToManager(manager);
        NJConfig config = NJConfigManager.getInstance();
        if (config != null) {
            config.nickSections = new ArrayList<>(manager.sections);
            NJConfigManager.save();
        }
    }

    private List<NickSection> deepCopySections(List<NickSection> original) {
        return original.stream().map(NickSection::copy).collect(Collectors.toList());
    }

    private void saveToNewSlot() {
        editor.syncToManager(manager);
        NJConfig config = NJConfigManager.getInstance();
        if (config != null) {
            if (config.savedNicknames == null) config.savedNicknames = new ArrayList<>();
            config.savedNicknames.add(new NJConfig.NickGroup("存檔", deepCopySections(manager.sections)));
            NJConfigManager.save();
        }
    }
}
