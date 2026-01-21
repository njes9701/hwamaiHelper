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
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> "enter";
            default -> {
                String name = GLFW.glfwGetKeyName(keyCode, 0);
                yield (name != null) ? name : "key_" + keyCode;
            }
        };
    }
}