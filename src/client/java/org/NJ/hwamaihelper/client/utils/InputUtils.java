package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

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
            InputUtil.Key inputUtilKey;

            switch (lowerKey) {
                case "left_ctrl":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.control");
                    break;
                case "right_ctrl":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.right.control");
                    break;
                case "ctrl", "control":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.control"); // Default to left
                    break;
                case "left_shift":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.shift");
                    break;
                case "right_shift":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.right.shift");
                    break;
                case "shift":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.shift"); // Default to left
                    break;
                case "left_alt":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.alt");
                    break;
                case "right_alt":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.right.alt");
                    break;
                case "alt":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.alt"); // Default to left
                    break;
                case "f1":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f1");
                    break;
                case "f2":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f2");
                    break;
                case "f3":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f3");
                    break;
                case "f4":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f4");
                    break;
                case "f5":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f5");
                    break;
                case "f6":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f6");
                    break;
                case "f7":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f7");
                    break;
                case "f8":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f8");
                    break;
                case "f9":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f9");
                    break;
                case "f10":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f10");
                    break;
                case "f11":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f11");
                    break;
                case "f12":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.f12");
                    break;
                case "win", "super", "cmd":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.left.win");
                    break;
                case "numpad_0":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.0");
                    break;
                case "numpad_1":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.1");
                    break;
                case "numpad_2":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.2");
                    break;
                case "numpad_3":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.3");
                    break;
                case "numpad_4":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.4");
                    break;
                case "numpad_5":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.5");
                    break;
                case "numpad_6":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.6");
                    break;
                case "numpad_7":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.7");
                    break;
                case "numpad_8":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.8");
                    break;
                case "numpad_9":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.9");
                    break;
                case "numpad_add":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.add");
                    break;
                case "numpad_subtract":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.subtract");
                    break;
                case "numpad_multiply":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.multiply");
                    break;
                case "numpad_divide":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.divide");
                    break;
                case "numpad_decimal":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.decimal");
                    break;
                case "numpad_enter":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.enter");
                    break;
                case "numpad_equal":
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard.keypad.equal");
                    break;
                case "mouse_left":
                    inputUtilKey = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_LEFT);
                    break;
                case "mouse_right":
                    inputUtilKey = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
                    break;
                case "mouse_middle":
                    inputUtilKey = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
                    break;
                default:
                    inputUtilKey = InputUtil.fromTranslationKey("key.keyboard." + lowerKey);
                    if (inputUtilKey == InputUtil.UNKNOWN_KEY) { // Corrected check
                        inputUtilKey = InputUtil.fromTranslationKey("key." + lowerKey);
                    }
                    break;
            }
            return inputUtilKey.getCode();
        } catch (Exception e) {
            return -1;
        }
    }
}