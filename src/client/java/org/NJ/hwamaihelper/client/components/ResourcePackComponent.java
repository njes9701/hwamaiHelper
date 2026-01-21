package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class ResourcePackComponent {
    private final ButtonWidget button;
    private final NJConfig config = NJConfigManager.getInstance();

    public ResourcePackComponent(int x, int y, int width, int height) {
        this.button = ButtonWidget.builder(getBtnText(), b -> {
            config.autoDisableResourcePack = !config.autoDisableResourcePack;
            b.setMessage(getBtnText());
        }).dimensions(x, y, width, height).build();
    }

    public void save(MinecraftClient client) {
        if (config.autoDisableResourcePack != config.lastAutoDisableStatus) {
            if (client.player != null) {
                String cmd = config.autoDisableResourcePack ? "chmc 設定 自己 取消使用材質包" : "chmc 設定 自己 使用材質包";
                client.player.networkHandler.sendChatCommand(cmd);
            }
            config.lastAutoDisableStatus = config.autoDisableResourcePack;
        }
    }

    public ButtonWidget getWidget() { return button; }
    private Text getBtnText() { return Text.of("材質包: " + (config.autoDisableResourcePack ? "§a自動取消" : "§c維持使用")); }
}