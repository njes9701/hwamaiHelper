package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.NJ.hwamaihelper.client.utils.KeyBindingComponent;
import org.NJ.hwamaihelper.client.utils.SimpleTextInputComponent;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;

public class PersonalSettingScreen implements NJTab {
    private final Minecraft client = Minecraft.getInstance();
    private KeyBindingComponent openMenuBinding;
    private KeyBindingComponent openWorkstationBinding;
    private KeyBindingComponent openGetItemBinding;
    private KeyBindingComponent gameModeWheelBinding;
    private KeyBindingComponent replenishFireworksBinding;
    private KeyBindingComponent englishSearchBinding;
    private KeyBindingComponent quickLandLevelingBinding;
    private KeyBindingComponent resourcePackBinding;
    private SimpleTextInputComponent wheelExcludeItemInput;
    private SimpleTextInputComponent quickLandLevelingTargetsInput;

    private int screenWidth;

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        int centerX = (width + 100) / 2;
        int startX = centerX - 150;
        NJConfig config = NJConfigManager.getInstance();

        this.openMenuBinding = new KeyBindingComponent(
                startX, 30, 300,
                "小助手主選單", config.openMenuKey, "X + F", config.openMenuOnRelease, config.openMenuEnabled,
                val -> config.openMenuOnRelease = val,
                val -> config.openMenuEnabled = val
        );

        this.openWorkstationBinding = new KeyBindingComponent(
                startX, 55, 300,
                "工作站介面", config.openWorkstationKey, "shift + G", config.openWorkstationOnRelease, config.openWorkstationEnabled,
                val -> config.openWorkstationOnRelease = val,
                val -> config.openWorkstationEnabled = val
        );

        this.openGetItemBinding = new KeyBindingComponent(
                startX, 80, 300,
                "取得物品介面", config.openGetItemKey, "G", config.openGetItemOnRelease, config.openGetItemEnabled,
                val -> config.openGetItemOnRelease = val,
                val -> config.openGetItemEnabled = val
        );

        this.gameModeWheelBinding = new KeyBindingComponent(
                startX, 105, 300,
                "遊戲模式轉盤", config.gameModeWheelKey, "alt", true, config.gameModeWheelEnabled,
                null, val -> config.gameModeWheelEnabled = val
        );

        this.wheelExcludeItemInput = new SimpleTextInputComponent(
                startX + 181, 105, 0, 58,
                "", config.gameModeWheelExcludeItem
        );
        this.wheelExcludeItemInput.setShowItemIcon(true);
        this.wheelExcludeItemInput.setOnMouseButtonEvent(() -> Minecraft.getInstance().setScreenAndShow(
                new ItemSelectionScreen(null, val -> {
                    this.wheelExcludeItemInput.setValue(val);
                    config.gameModeWheelExcludeItem = val;
                    NJConfigManager.save();
                })
        ));

        this.replenishFireworksBinding = new KeyBindingComponent(
                startX, 130, 300,
                "自動補充煙火", "", "", true, config.autoReplenishFireworks,
                null, val -> config.autoReplenishFireworks = val
        );

        this.englishSearchBinding = new KeyBindingComponent(
                startX, 155, 300,
                "英文搜尋翻譯", "", "", true, config.enableEnglishSearch,
                null, val -> config.enableEnglishSearch = val
        );

        this.quickLandLevelingBinding = new KeyBindingComponent(
                startX, 180, 300,
                "快速整地",
                normalizeQuickLandLevelingKey(config.quickLandLevelingKey),
                "未設定",
                config.quickLandLevelingToggleOnRelease,
                config.quickLandLevelingEnabled,
                null,
                val -> config.quickLandLevelingEnabled = val
        );
        this.quickLandLevelingTargetsInput = new SimpleTextInputComponent(
                startX + 180, 180, 0, 58,
                "", String.valueOf(clampQuickLandLevelingTargets(config.quickLandLevelingTargetsPerTick))
        );

        String packLabel = "自動套用資源包 (" + config.currentPackName + ")";
        this.resourcePackBinding = new KeyBindingComponent(
                startX, 205, 300,
                packLabel, "", "", true, config.resourcePackAlwaysApply,
                null, val -> org.NJ.hwamaihelper.client.logic.ResourcePackHandler.setEnabled(val)
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int centerX = (this.screenWidth + 100) / 2;
        int startX = centerX - 150;

        openMenuBinding.extractRenderState(context, mouseX, mouseY, delta);
        openWorkstationBinding.extractRenderState(context, mouseX, mouseY, delta);
        openGetItemBinding.extractRenderState(context, mouseX, mouseY, delta);
        gameModeWheelBinding.extractRenderState(context, mouseX, mouseY, delta);
        wheelExcludeItemInput.extractRenderState(context, mouseX, mouseY, delta);
        replenishFireworksBinding.extractRenderState(context, mouseX, mouseY, delta);
        englishSearchBinding.extractRenderState(context, mouseX, mouseY, delta);
        quickLandLevelingBinding.extractRenderState(context, mouseX, mouseY, delta);
        quickLandLevelingTargetsInput.extractRenderState(context, mouseX, mouseY, delta);
        resourcePackBinding.extractRenderState(context, mouseX, mouseY, delta);

        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 130 && mouseY <= 150) {
            context.setTooltipForNextFrame(client.font, Component.literal("當煙火數量偏低且正在使用時，自動補充煙火。"), mouseX, mouseY);
        }
        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 155 && mouseY <= 175) {
            context.setTooltipForNextFrame(client.font, Component.literal("在搜尋時自動提供英文翻譯，方便查找物品或內容。"), mouseX, mouseY);
        }
        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 180 && mouseY <= 200) {
            context.setTooltipForNextFrame(client.font, Component.literal("挖掘時會自動破壞玩家周圍所有可瞬間破壞方塊，可調整每 tick 破壞數量。"), mouseX, mouseY);
        }
        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 205 && mouseY <= 225) {
            context.setTooltipForNextFrame(client.font, Component.literal("加入伺服器時自動套用指定資源包。(實驗性功能)"), mouseX, mouseY);
        }
        if (mouseX >= startX + 172 && mouseX <= startX + 240 && mouseY >= 105 && mouseY <= 125) {
            NJConfig config = NJConfigManager.getInstance();
            String itemId = config.gameModeWheelExcludeItem;
            String itemName = "未知物品";

            if (itemId != null && !itemId.isEmpty()) {
                try {
                    Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
                    if (item != Items.AIR) {
                        itemName = Component.translatable(item.getDescriptionId()).getString();
                    } else {
                        itemName = itemId;
                    }
                } catch (Exception ignored) {
                    itemName = itemId;
                }
            }

            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("點擊可選擇要排除的物品"));
            lines.add(Component.literal("手持該物品時，不會觸發遊戲模式轉盤"));
            lines.add(Component.literal("目前設定: " + itemName));
            context.setTooltipForNextFrame(client.font, lines.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean d) {
        if (openMenuBinding.mouseClicked(click)) return true;
        if (openWorkstationBinding.mouseClicked(click)) return true;
        if (openGetItemBinding.mouseClicked(click)) return true;
        if (gameModeWheelBinding.mouseClicked(click)) return true;
        if (wheelExcludeItemInput.mouseClicked(click)) return true;
        if (replenishFireworksBinding.mouseClicked(click)) return true;
        if (englishSearchBinding.mouseClicked(click)) return true;
        if (quickLandLevelingTargetsInput.mouseClicked(click)) return true;
        if (quickLandLevelingBinding.mouseClicked(click)) return true;
        return resourcePackBinding.mouseClicked(click);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (openMenuBinding.keyPressed(input)) return true;
        if (openWorkstationBinding.keyPressed(input)) return true;
        if (openGetItemBinding.keyPressed(input)) return true;
        if (gameModeWheelBinding.keyPressed(input)) return true;
        if (wheelExcludeItemInput.keyPressed(input)) return true;
        if (replenishFireworksBinding.keyPressed(input)) return true;
        if (englishSearchBinding.keyPressed(input)) return true;
        if (quickLandLevelingTargetsInput.keyPressed(input)) return true;
        if (quickLandLevelingBinding.keyPressed(input)) return true;
        return resourcePackBinding.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        if (openMenuBinding.keyReleased(input)) return true;
        if (openWorkstationBinding.keyReleased(input)) return true;
        if (openGetItemBinding.keyReleased(input)) return true;
        if (gameModeWheelBinding.keyReleased(input)) return true;
        if (replenishFireworksBinding.keyReleased(input)) return true;
        if (englishSearchBinding.keyReleased(input)) return true;
        if (quickLandLevelingBinding.keyReleased(input)) return true;
        return resourcePackBinding.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (quickLandLevelingTargetsInput.charTyped(input)) return true;
        return wheelExcludeItemInput.charTyped(input);
    }

    @Override
    public void save() {
        NJConfig config = NJConfigManager.getInstance();
        config.openMenuKey = openMenuBinding.getValue();
        config.openWorkstationKey = openWorkstationBinding.getValue();
        config.openGetItemKey = openGetItemBinding.getValue();
        config.gameModeWheelKey = gameModeWheelBinding.getValue();
        config.quickLandLevelingKey = saveQuickLandLevelingKey(quickLandLevelingBinding.getValue());
        config.gameModeWheelExcludeItem = wheelExcludeItemInput.getValue();
        config.quickLandLevelingTargetsPerTick = parseQuickLandLevelingTargets(quickLandLevelingTargetsInput.getValue());
        quickLandLevelingTargetsInput.setValue(String.valueOf(config.quickLandLevelingTargetsPerTick));
        NJConfigManager.save();
    }

    private static String normalizeQuickLandLevelingKey(String value) {
        return (value == null || value.isBlank()) ? "未設定" : value;
    }

    private static String saveQuickLandLevelingKey(String value) {
        return (value == null || value.isBlank() || "未設定".equals(value)) ? "" : value;
    }

    private static int parseQuickLandLevelingTargets(String value) {
        if (value == null || value.isBlank()) {
            return 5;
        }
        String digitsOnly = value.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return 5;
        }
        try {
            return clampQuickLandLevelingTargets(Integer.parseInt(digitsOnly));
        } catch (NumberFormatException ignored) {
            return 5;
        }
    }

    private static int clampQuickLandLevelingTargets(int value) {
        return Math.clamp(value, 1, 10);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double h, double v) {
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double x, double y) {
        return false;
    }
}
