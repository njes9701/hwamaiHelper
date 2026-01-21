package org.NJ.hwamaihelper.client.logic;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class ConnectionHandler {
    public static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        NJConfig config = NJConfigManager.getInstance();
        if (config == null) return;

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                if (client.player != null) {
                    client.execute(() -> {
                        if (!config.hasInitializedPack) {
                            String packCmd = config.autoDisableResourcePack
                                    ? "chmc 設定 自己 取消使用材質包"
                                    : "chmc 設定 自己 使用材質包";
                            client.player.networkHandler.sendChatCommand(packCmd);
                            config.hasInitializedPack = true;
                        }

                        if (config.chunkLoadDistance != config.lastChunkDistance) {
                            client.player.networkHandler.sendChatCommand("chmc 設定 自己 強載入距離 " + config.chunkLoadDistance);
                            config.lastChunkDistance = config.chunkLoadDistance;
                        }
                        NJConfigManager.save();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
