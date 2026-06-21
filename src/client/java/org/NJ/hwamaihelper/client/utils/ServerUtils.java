package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ServerUtils {
    public static boolean isChungHwaServer(Minecraft client) {
        if (client == null) return false;
        ServerData serverInfo = client.getCurrentServer();
        if (serverInfo == null) return false;
        String address = serverInfo.ip.toLowerCase();
        return address.contains("mc.chunghwamc.com");
    }
}
