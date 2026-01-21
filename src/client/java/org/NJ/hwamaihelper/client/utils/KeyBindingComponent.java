package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyBindingComponent {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final String label;
    private final String defaultKey;
    private final ButtonWidget resetBtn;

    // 顯示框的座標與大小
    private final int x, y, width;
    private final int fieldX, fieldY, fieldW, fieldH;

    private boolean isRecording = false;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private String currentText;

    public KeyBindingComponent(int x, int y, int width, String label, String currentKey, String defaultKey) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.label = label;
        this.defaultKey = defaultKey;
        this.currentText = (currentKey != null) ? currentKey : defaultKey;

        int buttonWidth = 50;
        int spacing = 5;

        // 計算顯示框（原本 TextField 的位置）
        this.fieldW = buttonWidth; // 如果 Ctrl 還是太擠，可以把這裡改大一點，例如 60
        this.fieldH = 20;
        this.fieldX = x + width - (buttonWidth * 2) - spacing;
        this.fieldY = y;

        // 重置按鈕
        int resetX = x + width - buttonWidth;
        this.resetBtn = ButtonWidget.builder(Text.of("重置"), b -> {
            this.currentText = this.defaultKey;
            this.isRecording = false;
        }).dimensions(resetX, y, buttonWidth, 20).build();
    }

    private String convertToText(Set<Integer> keys) {
        if (keys.isEmpty()) return "";
        return keys.stream()
                .map(this::getKeyName)
                .collect(Collectors.joining(" + "));
    }

    private String getKeyName(int keyCode) {
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "Ctrl";
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "Shift";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "Alt";
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> "Enter";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            default -> {
                String name = GLFW.glfwGetKeyName(keyCode, 0);
                yield (name != null) ? name.toUpperCase() : "Key_" + keyCode;
            }
        };
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. 渲染左側標籤
        context.drawTextWithShadow(client.textRenderer, label, x, y + 6, -1);

        // 2. 繪製背景與邊框 (使用你源碼中的 drawStrokedRectangle)
        context.fill(fieldX, fieldY, fieldX + fieldW, fieldY + fieldH, 0xFF000000);
        int borderColor = isRecording ? 0xFFFFFFFF : 0xFF707070;
        context.drawStrokedRectangle(fieldX, fieldY, fieldW, fieldH, borderColor);

        // 3. 【修正重點】判斷顯示文字
        String display;
        if (isRecording) {
            // 只要按下過任何鍵，就不再顯示提示文字
            display = pressedKeys.isEmpty() ? "> 按下鍵 <" : currentText;
        } else {
            display = currentText;
        }

        // 4. 居中渲染
        int textWidth = client.textRenderer.getWidth(display);
        int tx = fieldX + (fieldW - textWidth) / 2;
        int ty = fieldY + (fieldH - 8) / 2;

        // 錄製時使用黃色或亮色，讓玩家知道正在輸入
        int textColor = isRecording ? 0xFFFFFF55 : -1;
        context.drawTextWithShadow(client.textRenderer, display, tx, ty, textColor);

        // 5. 渲染重置按鈕
        resetBtn.render(context, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(Click click) {
        if (resetBtn.mouseClicked(click, false)) return true;

        // 檢查是否點擊了自定義顯示框區域
        if (click.x() >= fieldX && click.x() <= fieldX + fieldW &&
                click.y() >= fieldY && click.y() <= fieldY + fieldH) {
            isRecording = true;
            pressedKeys.clear();
            return true;
        }

        isRecording = false;
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (isRecording) {
            int code = input.key();
            if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER || code == GLFW.GLFW_KEY_ESCAPE) {
                isRecording = false; // 這裡結束錄製後，render 就會切換回顯示 currentText
                return true;
            }
            pressedKeys.add(code); // 這裡加入集合
            this.currentText = convertToText(pressedKeys); // 同步更新保存用的變數
            return true;
        }
        return false;
    }

    public boolean keyReleased(KeyInput input) {
        if (isRecording) {
            return true;
        }
        return false;
    }

    public String getValue() {
        return currentText;
    }
}