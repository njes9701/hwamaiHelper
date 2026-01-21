package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class InputUtils {

    public static boolean isBindingPressed(MinecraftClient client, String keyStr) {
        if (keyStr == null || keyStr.isEmpty() || keyStr.contains(">")) return false; // 避開錄製中的提示文字
        for (String part : keyStr.toLowerCase().split("\\+")) {
            int code = getKeyCode(part.trim());
            if (code == -1 || !InputUtil.isKeyPressed(client.getWindow(), code)) {
                return false;
            }
        }
        return true;
    }

    public static int getKeyCode(String key) {
        try {
            String name = switch (key) {
                case "ctrl", "control" -> "left.control";
                case "alt" -> "left.alt";
                case "shift" -> "left.shift";
                case "win", "super", "cmd" -> "left.win";
                default -> key;
            };
            return InputUtil.fromTranslationKey("key.keyboard." + name).getCode();
        } catch (Exception e) {
            return -1;
        }
    }
}
