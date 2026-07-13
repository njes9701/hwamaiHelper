package org.NJ.hwamaihelper.client;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.NJ.hwamaihelper.client.logic.MalilibInputHandler;
import org.NJ.hwamaihelper.client.screens.CommandListScreen;
import org.NJ.hwamaihelper.client.screens.NickNameSettingScreen;
import org.NJ.hwamaihelper.config.HwamaiMalilibConfig;

import java.util.List;

/** Main HwamaiHelper screen implemented on MaLiLib's config GUI stack. */
public class NJMainScreen extends GuiConfigsBase {
    private ConfigTab selectedTab = ConfigTab.GENERAL;

    public NJMainScreen(Component title) {
        super(110, 50, HwamaiInitHandler.MOD_ID, null, title.getString());
        setTitle(title.getString());
    }

    @Override
    protected int getBrowserWidth() {
        int rightMargin = selectedTab == ConfigTab.GENERAL ? 55 : 10;
        return Math.max(100, this.width - getListX() - rightMargin);
    }

    @Override
    public void initGui() {
        super.initGui();

        addNavigationButton(10, 28, "一般設定", ConfigTab.GENERAL);
        addNavigationButton(10, 52, "快捷鍵", ConfigTab.HOTKEYS);

        addButton(new ButtonGeneric(10, 76, 90, 20, "自訂指令"),
                (button, mouseButton) -> GuiBase.openGui(new CommandListScreen().setParent(this)));
        addButton(new ButtonGeneric(10, 100, 90, 20, "暱稱編輯器"),
                (button, mouseButton) -> GuiBase.openGui(new NickNameSettingScreen().setParent(this)));

        int linkY = Math.max(132, this.height - 96);
        addLinkButton(linkY, "伺服器官網", "https://chunghwamc.com/");
        addLinkButton(linkY + 24, "華麥 Wiki", "https://chunghwamc.com/wiki/");
        addLinkButton(linkY + 48, "BlueMap", "https://chunghwamc.com/bluemap/");
    }

    private void addNavigationButton(int x, int y, String label, ConfigTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, 90, 20, label);
        button.setEnabled(selectedTab != tab);
        addButton(button, (pressed, mouseButton) -> {
            if (selectedTab != tab) {
                selectedTab = tab;
                clearElements();
                initGui();
            }
        });
    }

    private void addLinkButton(int y, String label, String url) {
        addButton(new ButtonGeneric(10, y, 90, 20, label),
                (button, mouseButton) -> Util.getPlatform().openUri(url));
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return switch (selectedTab) {
            case GENERAL -> ConfigOptionWrapper.createFor(HwamaiMalilibConfig.GENERAL_OPTIONS);
            case HOTKEYS -> ConfigOptionWrapper.createFor(HwamaiMalilibConfig.HOTKEY_OPTIONS);
        };
    }

    @Override
    public void removed() {
        super.removed();
        HwamaiMalilibConfig.INSTANCE.save();
        MalilibInputHandler.getInstance().refreshCustomCommandHotkeys();
    }

    private enum ConfigTab {
        GENERAL,
        HOTKEYS
    }
}
