package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import org.NJ.hwamaihelper.client.components.ResourcePackComponent;
import org.NJ.hwamaihelper.client.components.ChunkDistanceComponent;
import org.NJ.hwamaihelper.client.utils.KeyBindingComponent;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class PersonalSettingScreen implements NJTab {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private ResourcePackComponent resourcePackComp;
    private ChunkDistanceComponent distanceComp;
    private KeyBindingComponent openMenuBinding;
    private KeyBindingComponent openWorkstationBinding;
    private KeyBindingComponent openGetItemBinding;
    private ButtonWidget replenishFireworksBtn;

    private int screenWidth;

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        int centerX = (width + 100) / 2;
        NJConfig config = NJConfigManager.getInstance();

        this.resourcePackComp = new ResourcePackComponent(centerX - 100, 55, 200, 20);
        this.distanceComp = new ChunkDistanceComponent(centerX - 100, 80, 200, () -> this.init(width, height));

        // 使用新組件：標題、目前值、預設值
        this.openMenuBinding = new KeyBindingComponent(
                centerX - 100, 30, 200,
                "華麥小助手主開關", config.openMenuKey, "X + F"
        );

        this.openWorkstationBinding = new KeyBindingComponent(
                centerX - 100, 105, 200,
                "工作方塊介面開關", config.openWorkstationKey, "shift + G"
        );

        this.openGetItemBinding = new KeyBindingComponent(
                centerX - 100, 130, 200,
                "取得物品介面開關", config.openGetItemKey, "G"
        );

        this.replenishFireworksBtn = ButtonWidget.builder(getReplenishText(config), b -> {
            config.autoReplenishFireworks = !config.autoReplenishFireworks;
            b.setMessage(getReplenishText(config));
        }).dimensions(centerX - 100, 155, 200, 20).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        resourcePackComp.getWidget().render(context, mouseX, mouseY, delta);
        distanceComp.getSlider().render(context, mouseX, mouseY, delta);
        distanceComp.getResetBtn().render(context, mouseX, mouseY, delta);

        // 渲染快捷鍵組件
        openMenuBinding.render(context, mouseX, mouseY, delta);
        openWorkstationBinding.render(context, mouseX, mouseY, delta);
        openGetItemBinding.render(context, mouseX, mouseY, delta);
        replenishFireworksBtn.render(context, mouseX, mouseY, delta);

        if (replenishFireworksBtn.isMouseOver(mouseX, mouseY)) {
            context.drawTooltip(client.textRenderer, Text.of("當煙火數量小於5個且使用時自動補充"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean d) {
        // 優先處理滑塊焦點邏輯，確保點擊其他地方時取消滑塊焦點
        boolean clickedSlider = distanceComp.getSlider().mouseClicked(click, d);
        distanceComp.getSlider().setFocused(clickedSlider);
        if (clickedSlider) return true;

        if (resourcePackComp.getWidget().mouseClicked(click, d)) return true;
        if (distanceComp.getResetBtn().mouseClicked(click, d)) return true;
        if (replenishFireworksBtn.mouseClicked(click, d)) return true;

        // 修正點：直接傳入整個 click 物件，不要拆開
        if (openMenuBinding.mouseClicked(click)) return true;
        if (openWorkstationBinding.mouseClicked(click)) return true;
        return openGetItemBinding.mouseClicked(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        // 修正點：直接傳入整個 input 物件
        if (openMenuBinding.keyPressed(input)) return true;
        if (openWorkstationBinding.keyPressed(input)) return true;
        return openGetItemBinding.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        // 修正點：直接傳入整個 input 物件
        if (openMenuBinding.keyReleased(input)) return true;
        if (openWorkstationBinding.keyReleased(input)) return true;
        return openGetItemBinding.keyReleased(input);
    }

    @Override
    public void save() {
        resourcePackComp.save(client);
        distanceComp.save(client);

        NJConfig config = NJConfigManager.getInstance();
        config.openMenuKey = openMenuBinding.getValue();
        config.openWorkstationKey = openWorkstationBinding.getValue();
        config.openGetItemKey = openGetItemBinding.getValue();
        NJConfigManager.save();
    }

    @Override public boolean mouseScrolled(double x, double y, double h, double v) { return false; }
    @Override public boolean charTyped(CharInput i) { return false; }
    @Override public boolean mouseDragged(Click click, double x, double y) {
        if (distanceComp.getSlider().isFocused() && distanceComp.getSlider().mouseDragged(click, x, y)) {
            return true;
        }
        return false;
    }

    private Text getReplenishText(NJConfig config) {
        return Text.of("煙火自動補充: " + (config.autoReplenishFireworks ? "§a開啟" : "§c關閉"));
    }
}