package org.NJ.hwamaihelper.client.logic;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class ConnectionHandler {
    public static void onJoin(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        org.NJ.hwamaihelper.client.logic.ResourcePackHandler.resetCheck();
    }
}
