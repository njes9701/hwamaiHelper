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
            String lowerKey = key.toLowerCase();
            String name = switch (lowerKey) {
                case "ctrl", "control" -> "left.control";
                case "alt" -> "left.alt";
                case "shift" -> "left.shift";
                case "win", "super", "cmd" -> "left.win";
                case "numpad_0" -> "keypad.0";
                case "numpad_1" -> "keypad.1";
                case "numpad_2" -> "keypad.2";
                case "numpad_3" -> "keypad.3";
                case "numpad_4" -> "keypad.4";
                case "numpad_5" -> "keypad.5";
                case "numpad_6" -> "keypad.6";
                case "numpad_7" -> "keypad.7";
                case "numpad_8" -> "keypad.8";
                case "numpad_9" -> "keypad.9";
                case "numpad_add" -> "keypad.add";
                case "numpad_subtract" -> "keypad.subtract";
                case "numpad_multiply" -> "keypad.multiply";
                case "numpad_divide" -> "keypad.divide";
                case "numpad_decimal" -> "keypad.decimal";
                case "numpad_enter" -> "keypad.enter";
                case "numpad_equal" -> "keypad.equal";
                default -> key;
            };
            return InputUtil.fromTranslationKey("key.keyboard." + name).getCode();
        } catch (Exception e) {
            return -1;
        }
    }
}
