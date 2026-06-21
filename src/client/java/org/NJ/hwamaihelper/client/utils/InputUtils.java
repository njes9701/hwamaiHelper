package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class InputUtils {

    public static boolean isBindingPressed(Minecraft client, String keyStr) {
        if (keyStr == null || keyStr.isEmpty() || keyStr.contains(">")) return false; // 避開錄製中的提示文字
        for (String part : keyStr.toLowerCase().split("\\+")) {
            int code = getKeyCode(part.trim());
            if (code == -1 || !InputConstants.isKeyDown(client.getWindow(), code)) {
                return false;
            }
        }
        return true;
    }

    public static int getKeyCode(String key) {
        try {
            String lowerKey = key.toLowerCase();
            InputConstants.Key inputUtilKey;

            switch (lowerKey) {
                case "left_ctrl":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.control");
                    break;
                case "right_ctrl":
                    inputUtilKey = InputConstants.getKey("key.keyboard.right.control");
                    break;
                case "ctrl", "control":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.control"); // Default to left
                    break;
                case "left_shift":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.shift");
                    break;
                case "right_shift":
                    inputUtilKey = InputConstants.getKey("key.keyboard.right.shift");
                    break;
                case "shift":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.shift"); // Default to left
                    break;
                case "left_alt":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.alt");
                    break;
                case "right_alt":
                    inputUtilKey = InputConstants.getKey("key.keyboard.right.alt");
                    break;
                case "alt":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.alt"); // Default to left
                    break;
                case "f1":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f1");
                    break;
                case "f2":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f2");
                    break;
                case "f3":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f3");
                    break;
                case "f4":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f4");
                    break;
                case "f5":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f5");
                    break;
                case "f6":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f6");
                    break;
                case "f7":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f7");
                    break;
                case "f8":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f8");
                    break;
                case "f9":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f9");
                    break;
                case "f10":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f10");
                    break;
                case "f11":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f11");
                    break;
                case "f12":
                    inputUtilKey = InputConstants.getKey("key.keyboard.f12");
                    break;
                case "win", "super", "cmd":
                    inputUtilKey = InputConstants.getKey("key.keyboard.left.win");
                    break;
                case "numpad_0":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.0");
                    break;
                case "numpad_1":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.1");
                    break;
                case "numpad_2":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.2");
                    break;
                case "numpad_3":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.3");
                    break;
                case "numpad_4":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.4");
                    break;
                case "numpad_5":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.5");
                    break;
                case "numpad_6":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.6");
                    break;
                case "numpad_7":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.7");
                    break;
                case "numpad_8":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.8");
                    break;
                case "numpad_9":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.9");
                    break;
                case "numpad_add":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.add");
                    break;
                case "numpad_subtract":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.subtract");
                    break;
                case "numpad_multiply":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.multiply");
                    break;
                case "numpad_divide":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.divide");
                    break;
                case "numpad_decimal":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.decimal");
                    break;
                case "numpad_enter":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.enter");
                    break;
                case "numpad_equal":
                    inputUtilKey = InputConstants.getKey("key.keyboard.keypad.equal");
                    break;
                case "mouse_left":
                    inputUtilKey = InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_LEFT);
                    break;
                case "mouse_right":
                    inputUtilKey = InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
                    break;
                case "mouse_middle":
                    inputUtilKey = InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
                    break;
                default:
                    inputUtilKey = InputConstants.getKey("key.keyboard." + lowerKey);
                    if (inputUtilKey == InputConstants.UNKNOWN) { // Corrected check
                        inputUtilKey = InputConstants.getKey("key." + lowerKey);
                    }
                    break;
            }
            return inputUtilKey.getValue();
        } catch (Exception e) {
            return -1;
        }
    }
}
