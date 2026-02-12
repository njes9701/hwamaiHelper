package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import java.util.ArrayList;
import java.util.List;
import org.NJ.hwamaihelper.client.utils.KeyBindingComponent;
import org.NJ.hwamaihelper.client.utils.SimpleTextInputComponent;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class PersonalSettingScreen implements NJTab {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private KeyBindingComponent openMenuBinding;
    private KeyBindingComponent openWorkstationBinding;
    private KeyBindingComponent openGetItemBinding;
    private KeyBindingComponent gameModeWheelBinding;
    private KeyBindingComponent replenishFireworksBinding;
    private KeyBindingComponent englishSearchBinding;
    private SimpleTextInputComponent wheelExcludeItemInput;

    private int screenWidth;

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        int centerX = (width + 100) / 2;
        int startX = centerX - 150; // 統一靠左對齊點
        NJConfig config = NJConfigManager.getInstance();

        this.openMenuBinding = new KeyBindingComponent(
                startX, 30, 300,
                "小助手主開關", config.openMenuKey, "X + F", config.openMenuOnRelease, config.openMenuEnabled,
                val -> config.openMenuOnRelease = val,
                val -> config.openMenuEnabled = val
        );

        this.openWorkstationBinding = new KeyBindingComponent(
                startX, 55, 300,
                "工作方塊介面", config.openWorkstationKey, "shift + G", config.openWorkstationOnRelease, config.openWorkstationEnabled,
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
                "模式切換轉盤", config.gameModeWheelKey, "alt", true, config.gameModeWheelEnabled,
                null, val -> config.gameModeWheelEnabled = val
        );

        this.wheelExcludeItemInput = new SimpleTextInputComponent(
                startX + 181, 105, 0, 58,
                "", config.gameModeWheelExcludeItem
        );
        this.wheelExcludeItemInput.setShowItemIcon(true);
        this.wheelExcludeItemInput.setOnClick(() -> {
            MinecraftClient.getInstance().setScreen(new ItemSelectionScreen(MinecraftClient.getInstance().currentScreen, val -> {
                this.wheelExcludeItemInput.setValue(val);
                // 立即存檔以確保變更生效
                config.gameModeWheelExcludeItem = val;
                org.NJ.hwamaihelper.config.NJConfigManager.save();
            }));
        });

        this.replenishFireworksBinding = new KeyBindingComponent(
                startX, 130, 300,
                "煙火自動補充", "", "", true, config.autoReplenishFireworks,
                null, val -> config.autoReplenishFireworks = val
        );

        this.englishSearchBinding = new KeyBindingComponent(
                startX, 155, 300,
                "創造物品英文搜尋", "", "", true, config.enableEnglishSearch,
                null, val -> config.enableEnglishSearch = val
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = (this.screenWidth + 100) / 2;
        int startX = centerX - 150;

        // 渲染快捷鍵與功能組件
        openMenuBinding.render(context, mouseX, mouseY, delta);
        openWorkstationBinding.render(context, mouseX, mouseY, delta);
        openGetItemBinding.render(context, mouseX, mouseY, delta);
        gameModeWheelBinding.render(context, mouseX, mouseY, delta);
        wheelExcludeItemInput.render(context, mouseX, mouseY, delta);
        replenishFireworksBinding.render(context, mouseX, mouseY, delta);
        englishSearchBinding.render(context, mouseX, mouseY, delta);

        // 額外繪製煙火補充的 Tooltip
        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 130 && mouseY <= 150) {
             context.drawTooltip(client.textRenderer, Text.of("當煙火數量小於5個且使用時自動補充"), mouseX, mouseY);
        }
        if (mouseX >= startX && mouseX <= startX + 300 && mouseY >= 155 && mouseY <= 175) {
             context.drawTooltip(client.textRenderer, Text.of("開啟後可在創造模式內使用英文搜尋物品"), mouseX, mouseY);
        }
        if (mouseX >= startX + 172 && mouseX <= startX + 172 + 68 && mouseY >= 105 && mouseY <= 125) {
             NJConfig config = org.NJ.hwamaihelper.config.NJConfigManager.getInstance();
             String itemId = config.gameModeWheelExcludeItem;
             String itemName = "未設定";
             
             if (itemId != null && !itemId.isEmpty()) {
                 try {
                     net.minecraft.item.Item item = net.minecraft.registry.Registries.ITEM.get(net.minecraft.util.Identifier.of(itemId));
                     if (item != net.minecraft.item.Items.AIR) {
                         itemName = item.getName().getString();
                     } else {
                         itemName = itemId;
                     }
                 } catch (Exception ignored) {
                     itemName = itemId;
                 }
             }
             
             List<Text> lines = new java.util.ArrayList<>();
             lines.add(Text.of("§b排除手持物設定"));
             lines.add(Text.of("§7當手持此物品時，按下轉盤按鍵不會觸發模式切換。"));
             lines.add(Text.of("§e目前設定: §f" + itemName));
             
             context.drawTooltip(client.textRenderer, lines, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean d) {
        if (openMenuBinding.mouseClicked(click)) return true;
        if (openWorkstationBinding.mouseClicked(click)) return true;
        if (openGetItemBinding.mouseClicked(click)) return true;
        if (gameModeWheelBinding.mouseClicked(click)) return true;
        if (wheelExcludeItemInput.mouseClicked(click)) return true;
        if (replenishFireworksBinding.mouseClicked(click)) return true;
        return englishSearchBinding.mouseClicked(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (openMenuBinding.keyPressed(input)) return true;
        if (openWorkstationBinding.keyPressed(input)) return true;
        if (openGetItemBinding.keyPressed(input)) return true;
        if (gameModeWheelBinding.keyPressed(input)) return true;
        if (wheelExcludeItemInput.keyPressed(input)) return true;
        if (replenishFireworksBinding.keyPressed(input)) return true;
        return englishSearchBinding.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (openMenuBinding.keyReleased(input)) return true;
        if (openWorkstationBinding.keyReleased(input)) return true;
        if (openGetItemBinding.keyReleased(input)) return true;
        if (gameModeWheelBinding.keyReleased(input)) return true;
        if (replenishFireworksBinding.keyReleased(input)) return true;
        return englishSearchBinding.keyReleased(input);
    }
    
    @Override
    public boolean charTyped(CharInput input) {
        return wheelExcludeItemInput.charTyped(input);
    }

    @Override
    public void save() {
        NJConfig config = NJConfigManager.getInstance();

        config.openMenuKey = openMenuBinding.getValue();
        config.openWorkstationKey = openWorkstationBinding.getValue();
        config.openGetItemKey = openGetItemBinding.getValue();
        config.gameModeWheelKey = gameModeWheelBinding.getValue();
        config.gameModeWheelExcludeItem = wheelExcludeItemInput.getValue();
        NJConfigManager.save();
    }

    @Override public boolean mouseScrolled(double x, double y, double h, double v) { return false; }
    @Override public boolean mouseDragged(Click click, double x, double y) { return false; }
}