package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class ServerUtils {
    public static boolean isChungHwaServer(MinecraftClient client) {
        if (client == null) return false;
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) return false;
        String address = serverInfo.address.toLowerCase();
        return address.contains("mc.chunghwamc.com");
    }
}
