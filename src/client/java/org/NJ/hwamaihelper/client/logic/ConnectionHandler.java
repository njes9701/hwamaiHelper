package org.NJ.hwamaihelper.client.logic;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class ConnectionHandler {
    public static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        // No-op: Resource pack logic removed
    }
}
