package org.NJ.hwamaihelper.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;
import org.NJ.hwamaihelper.client.screens.CommandListScreen;
import org.NJ.hwamaihelper.client.screens.NJTab;
import org.NJ.hwamaihelper.client.screens.PersonalSettingScreen;
import org.NJ.hwamaihelper.client.screens.NickNameSettingScreen; // 1. 導入新分頁

public class NJMainScreen extends Screen {
    private NJTab currentTab;
    private final CommandListScreen commandListTab = new CommandListScreen();
    private final PersonalSettingScreen personalSettingTab = new PersonalSettingScreen();
    private final NickNameSettingScreen nickNameSettingTab = new NickNameSettingScreen(); // 2. 實例化

    public NJMainScreen(Text title) {
        super(title);
        NJConfig config = NJConfigManager.getInstance();
        this.currentTab = switch (config == null ? 0 : config.lastTabIndex) {
            case 1 -> personalSettingTab;
            case 2 -> nickNameSettingTab;
            default -> commandListTab;
        };
    }

    @Override
    protected void init() {

        // 華麥設定按鈕 (座標往下移)
        this.addDrawableChild(ButtonWidget.builder(Text.of("華麥設定"), button -> switchTab(personalSettingTab))
                .dimensions(10, 40, 80, 20).build());

        // 暱稱設定按鈕 (新增)
        this.addDrawableChild(ButtonWidget.builder(Text.of("暱稱設定"), button -> switchTab(nickNameSettingTab))
                .dimensions(10, 65, 80, 20).build());

        // 指令集按鈕
        this.addDrawableChild(ButtonWidget.builder(Text.of("指令集"), button -> switchTab(commandListTab))
                .dimensions(10, 90, 80, 20).build());


        this.addDrawableChild(ButtonWidget.builder(Text.of("§b華麥街景地圖"), button -> {
            Util.getOperatingSystem().open("https://chunghwamc.com/bluemap/");
        }).dimensions(10, 200, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("§b華麥衛星地圖"), button -> {
            Util.getOperatingSystem().open("https://chunghwamc.com/map/#YouliYuersworld;flat;160,64,260;15");
        }).dimensions(10, 175, 80, 20).build());

        // 中華麥塊網頁
        this.addDrawableChild(ButtonWidget.builder(Text.of("§d中華麥塊網頁"), button -> Util.getOperatingSystem().open("https://chunghwamc.com/"))
                .dimensions(10, 225, 80, 20).build());

        // 中華麥塊Wiki
        this.addDrawableChild(ButtonWidget.builder(Text.of("§e中華麥塊Wiki"), button -> Util.getOperatingSystem().open("https://chunghwamc.com/wiki/"))
                .dimensions(10, 250, 80, 20).build());

        currentTab.init(this.width, this.height);
    }

    private void switchTab(NJTab tab) {
        if (this.currentTab != tab) {
            this.currentTab.save();
            this.currentTab = tab;
            this.currentTab.init(this.width, this.height);

            NJConfig config = NJConfigManager.getInstance();
            if (config != null) {
                int idx = 0;
                if (tab == personalSettingTab) idx = 1;
                else if (tab == nickNameSettingTab) idx = 2;

                config.lastTabIndex = idx;
                NJConfigManager.save();
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        context.fill(0, 0, 100, this.height, 0x44000000);
        context.fill(100, 0, 101, this.height, 0x44FFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, this.title, (this.width + 100) / 2, 15, 0xFFFFFF);

        if (currentTab != null) {
            currentTab.render(context, mouseX, mouseY, delta);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (currentTab != null && currentTab.mouseClicked(click, doubled)) {
            this.setFocused(null);
            if (click.button() == 0) this.setDragging(true);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (currentTab != null && currentTab.mouseDragged(click, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean charTyped(CharInput input) {
        // 修正：在最新的 Minecraft 版本中，如果參數不匹配，請直接傳入 char 和 int
        if (currentTab != null && currentTab.charTyped(input)) return true;
        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (currentTab != null && currentTab.keyPressed(input)) return true;

        // 256 是 ESC 鍵
        if (input.key() == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (currentTab != null && currentTab.keyReleased(input)) return true;
        return super.keyReleased(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (currentTab != null && currentTab.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        if (currentTab != null) currentTab.save();
        super.close();
    }

    @Override
    public void removed() {
        if (this.currentTab != null) {
            this.currentTab.save();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}