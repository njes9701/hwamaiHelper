package org.NJ.hwamaihelper.client.utils;

import org.lwjgl.glfw.GLFW;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyRecorder {

    // 將當前按下的多個 KeyCode 集合轉換為 "ctrl+shift+a" 格式
    public static String convertToText(Set<Integer> pressedKeys) {
        if (pressedKeys.isEmpty()) return "";
        return pressedKeys.stream()
                .map(KeyRecorder::getKeyName)
                .collect(Collectors.joining("+"));
    }

    private static String getKeyName(int keyCode) {
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "ctrl";
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "shift";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "alt";
            case GLFW.GLFW_KEY_ENTER -> "enter";
            case GLFW.GLFW_KEY_KP_ENTER -> "numpad_enter";
            case GLFW.GLFW_KEY_KP_0 -> "numpad_0";
            case GLFW.GLFW_KEY_KP_1 -> "numpad_1";
            case GLFW.GLFW_KEY_KP_2 -> "numpad_2";
            case GLFW.GLFW_KEY_KP_3 -> "numpad_3";
            case GLFW.GLFW_KEY_KP_4 -> "numpad_4";
            case GLFW.GLFW_KEY_KP_5 -> "numpad_5";
            case GLFW.GLFW_KEY_KP_6 -> "numpad_6";
            case GLFW.GLFW_KEY_KP_7 -> "numpad_7";
            case GLFW.GLFW_KEY_KP_8 -> "numpad_8";
            case GLFW.GLFW_KEY_KP_9 -> "numpad_9";
            case GLFW.GLFW_KEY_KP_ADD -> "numpad_add";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "numpad_subtract";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "numpad_multiply";
            case GLFW.GLFW_KEY_KP_DIVIDE -> "numpad_divide";
            case GLFW.GLFW_KEY_KP_DECIMAL -> "numpad_decimal";
            case GLFW.GLFW_KEY_KP_EQUAL -> "numpad_equal";
            default -> {
                String name = GLFW.glfwGetKeyName(keyCode, 0);
                yield (name != null) ? name : "key_" + keyCode;
            }
        };
    }
}