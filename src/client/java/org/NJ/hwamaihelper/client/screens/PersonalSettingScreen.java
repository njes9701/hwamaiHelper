package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import org.NJ.hwamaihelper.client.utils.KeyBindingComponent;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class PersonalSettingScreen implements NJTab {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private KeyBindingComponent resourcePackBinding;
    private KeyBindingComponent openMenuBinding;
    private KeyBindingComponent openWorkstationBinding;
    private KeyBindingComponent openGetItemBinding;
    private KeyBindingComponent gameModeWheelBinding;
    private KeyBindingComponent replenishFireworksBinding;

    private int screenWidth;

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        int centerX = (width + 100) / 2;
        int startX = centerX - 150; // 統一靠左對齊點
        NJConfig config = NJConfigManager.getInstance();

        // 1. 材質包自動取消 (不需綁定鍵)
        this.resourcePackBinding = new KeyBindingComponent(
                startX, 30, 300,
                "材質包自動取消", "", "", true, config.autoDisableResourcePack,
                null, val -> config.autoDisableResourcePack = val
        );

        this.openMenuBinding = new KeyBindingComponent(
                startX, 55, 300,
                "小助手主開關", config.openMenuKey, "X + F", config.openMenuOnRelease, config.openMenuEnabled,
                val -> config.openMenuOnRelease = val,
                val -> config.openMenuEnabled = val
        );

        this.openWorkstationBinding = new KeyBindingComponent(
                startX, 80, 300,
                "工作方塊介面", config.openWorkstationKey, "shift + G", config.openWorkstationOnRelease, config.openWorkstationEnabled,
                val -> config.openWorkstationOnRelease = val,
                val -> config.openWorkstationEnabled = val
        );

        this.openGetItemBinding = new KeyBindingComponent(
                startX, 105, 300,
                "取得物品介面", config.openGetItemKey, "G", config.openGetItemOnRelease, config.openGetItemEnabled,
                val -> config.openGetItemOnRelease = val,
                val -> config.openGetItemEnabled = val
        );

        this.gameModeWheelBinding = new KeyBindingComponent(
                startX, 130, 300,
                "模式切換轉盤", config.gameModeWheelKey, "alt", true, config.gameModeWheelEnabled,
                null, val -> config.gameModeWheelEnabled = val
        );

        this.replenishFireworksBinding = new KeyBindingComponent(
                startX, 155, 300,
                "煙火自動補充", "", "", true, config.autoReplenishFireworks,
                null, val -> config.autoReplenishFireworks = val
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染快捷鍵與功能組件
        resourcePackBinding.render(context, mouseX, mouseY, delta);
        openMenuBinding.render(context, mouseX, mouseY, delta);
        openWorkstationBinding.render(context, mouseX, mouseY, delta);
        openGetItemBinding.render(context, mouseX, mouseY, delta);
        gameModeWheelBinding.render(context, mouseX, mouseY, delta);
        replenishFireworksBinding.render(context, mouseX, mouseY, delta);

        // 額外繪製煙火補充的 Tooltip
        int centerX = (this.screenWidth + 100) / 2;
        if (mouseX >= centerX - 150 && mouseX <= centerX + 150 && mouseY >= 155 && mouseY <= 175) {
             context.drawTooltip(client.textRenderer, Text.of("當煙火數量小於5個且使用時自動補充"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean d) {
        if (resourcePackBinding.mouseClicked(click)) return true;
        if (openMenuBinding.mouseClicked(click)) return true;
        if (openWorkstationBinding.mouseClicked(click)) return true;
        if (openGetItemBinding.mouseClicked(click)) return true;
        if (gameModeWheelBinding.mouseClicked(click)) return true;
        return replenishFireworksBinding.mouseClicked(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (resourcePackBinding.keyPressed(input)) return true;
        if (openMenuBinding.keyPressed(input)) return true;
        if (openWorkstationBinding.keyPressed(input)) return true;
        if (openGetItemBinding.keyPressed(input)) return true;
        if (gameModeWheelBinding.keyPressed(input)) return true;
        return replenishFireworksBinding.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (resourcePackBinding.keyReleased(input)) return true;
        if (openMenuBinding.keyReleased(input)) return true;
        if (openWorkstationBinding.keyReleased(input)) return true;
        if (openGetItemBinding.keyReleased(input)) return true;
        if (gameModeWheelBinding.keyReleased(input)) return true;
        return replenishFireworksBinding.keyReleased(input);
    }

    @Override
    public void save() {
        NJConfig config = NJConfigManager.getInstance();

        // 處理材質包自動取消的儲存與立即套用邏輯
        if (config.autoDisableResourcePack != config.lastAutoDisableStatus) {
            if (client.player != null) {
                String cmd = config.autoDisableResourcePack ? "chmc 設定 自己 取消使用材質包" : "chmc 設定 自己 使用材質包";
                client.player.networkHandler.sendChatCommand(cmd);
            }
            config.lastAutoDisableStatus = config.autoDisableResourcePack;
        }

        config.openMenuKey = openMenuBinding.getValue();
        config.openWorkstationKey = openWorkstationBinding.getValue();
        config.openGetItemKey = openGetItemBinding.getValue();
        config.gameModeWheelKey = gameModeWheelBinding.getValue();
        NJConfigManager.save();
    }

    @Override public boolean mouseScrolled(double x, double y, double h, double v) { return false; }
    @Override public boolean charTyped(CharInput i) { return false; }
    @Override public boolean mouseDragged(Click click, double x, double y) { return false; }
}