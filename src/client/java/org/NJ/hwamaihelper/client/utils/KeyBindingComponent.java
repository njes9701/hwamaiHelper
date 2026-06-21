package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyBindingComponent {
    private final Minecraft client = Minecraft.getInstance();
    private final String label;
    private final String defaultKey;
    private final Button resetBtn;
    private final Button triggerModeBtn;
    private final Button toggleBtn;

    // 顯示框的座標與大小
    private final int x, y, width;
    private final int fieldX, fieldY, fieldW, fieldH;

    private boolean isRecording = false;
    private boolean onRelease = true;
    private boolean enabled = true;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private String currentComponent;
    private java.util.function.Consumer<Boolean> onTriggerModeChanged;
    private java.util.function.Consumer<Boolean> onToggleChanged;

    public KeyBindingComponent(int x, int y, int width, String label, String currentKey, String defaultKey, 
                               boolean initialOnRelease, boolean initialEnabled,
                               java.util.function.Consumer<Boolean> onTriggerModeChanged,
                               java.util.function.Consumer<Boolean> onToggleChanged) {
        this.x = x; 
        this.y = y;
        this.width = width; // 這裡的 width 將作為參考總寬
        this.label = label;
        this.defaultKey = defaultKey;
        this.currentComponent = (currentKey != null) ? currentKey : defaultKey;
        this.onRelease = initialOnRelease;
        this.enabled = initialEnabled;
        this.onTriggerModeChanged = onTriggerModeChanged;
        this.onToggleChanged = onToggleChanged;

        // 定義四個區域的固定起始點 (相對於 x)
        // 1. 功能敘述區: 0
        // 2. 按鍵綁定區: 110
        // 3. 主開關區: 180 (包含模式與開關)
        // 4. 按鍵重置區: 280
        
        int bindingX = x + 110;
        int switchesX = x + 180;
        int resetX = x + 280;

        int modeButtonWidth = 60; // 增加寬度以容納「放開觸發」
        int toggleButtonWidth = 32;
        int resetButtonWidth = 35;
        int spacing = 3;

        // 2. 按鍵綁定區 (顯示框)
        this.fieldW = 60;
        this.fieldH = 20;
        this.fieldX = bindingX;
        this.fieldY = y;

        // 3. 主開關區 (模式按鈕 + 開關按鈕)
        this.triggerModeBtn = Button.builder(getTriggerModeComponent(), b -> {
            this.onRelease = !this.onRelease;
            b.setMessage(getTriggerModeComponent());
            if (this.onTriggerModeChanged != null) {
                this.onTriggerModeChanged.accept(this.onRelease);
            }
        }).bounds(switchesX, y, modeButtonWidth, 20).build();

        this.toggleBtn = Button.builder(getToggleComponent(), b -> {
            this.enabled = !this.enabled;
            b.setMessage(getToggleComponent());
            if (this.onToggleChanged != null) {
                this.onToggleChanged.accept(this.enabled);
            }
        }).bounds(switchesX + modeButtonWidth + spacing, y, toggleButtonWidth, 20).build();

        // 4. 按鍵重置區 (重置按鈕)
        this.resetBtn = Button.builder(Component.literal("重置"), b -> {
            this.currentComponent = this.defaultKey;
            this.isRecording = false;
        }).bounds(resetX, y, resetButtonWidth, 20).build();
    }

    private Component getTriggerModeComponent() {
        return Component.literal(onRelease ? "§7放開觸發" : "§b按下觸發");
    }

    private Component getToggleComponent() {
        return Component.literal(enabled ? "§a開" : "§c關");
    }

    private String convertToComponent(Set<Integer> keys) {
        if (keys.isEmpty()) return "";
        return keys.stream()
                .map(this::getKeyName)
                .collect(Collectors.joining(" + "));
    }

    private String getKeyName(int keyCode) {
        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return "Mouse_Left";
        }
        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return "Mouse_Right";
        }
        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            return "Mouse_Middle";
        }
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left_Ctrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right_Ctrl";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left_Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right_Shift";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left_Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right_Alt";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_KP_ENTER -> "NumPad_Enter";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_KP_0 -> "NumPad_0";
            case GLFW.GLFW_KEY_KP_1 -> "NumPad_1";
            case GLFW.GLFW_KEY_KP_2 -> "NumPad_2";
            case GLFW.GLFW_KEY_KP_3 -> "NumPad_3";
            case GLFW.GLFW_KEY_KP_4 -> "NumPad_4";
            case GLFW.GLFW_KEY_KP_5 -> "NumPad_5";
            case GLFW.GLFW_KEY_KP_6 -> "NumPad_6";
            case GLFW.GLFW_KEY_KP_7 -> "NumPad_7";
            case GLFW.GLFW_KEY_KP_8 -> "NumPad_8";
            case GLFW.GLFW_KEY_KP_9 -> "NumPad_9";
            case GLFW.GLFW_KEY_KP_ADD -> "NumPad_Add";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "NumPad_Subtract";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "NumPad_Multiply";
            case GLFW.GLFW_KEY_KP_DIVIDE -> "NumPad_Divide";
            case GLFW.GLFW_KEY_KP_DECIMAL -> "NumPad_Decimal";
            case GLFW.GLFW_KEY_KP_EQUAL -> "NumPad_Equal";
            default -> {
                if (keyCode <= GLFW.GLFW_KEY_UNKNOWN) {
                    yield "Key_" + keyCode;
                }
                String name = GLFW.glfwGetKeyName(keyCode, 0);
                yield (name != null) ? name.toUpperCase() : "Key_" + keyCode;
            }
        };
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // 1. 渲染左側標籤
        context.text(client.font, label, x, y + 6, -1);

        // 2. 只有在有按鍵綁定內容時才繪製背景與邊框
        if (currentComponent != null && !currentComponent.isEmpty()) {
            context.fill(fieldX, fieldY, fieldX + fieldW, fieldY + fieldH, 0xFF000000);
            int borderColor = isRecording ? 0xFFFFFFFF : 0xFF707070;
            context.outline(fieldX, fieldY, fieldW, fieldH, borderColor);

            // 3. 【修正重點】判斷顯示文字
            String display;
            if (isRecording) {
                // 只要按下過任何鍵，就不再顯示提示文字
                display = pressedKeys.isEmpty() ? "> 按下鍵 <" : currentComponent;
            } else {
                display = currentComponent;
            }
            String displayComponent = display; // Use a new variable for the potentially truncated text

            int maxComponentWidth = fieldW - 4; // Allow some padding (e.g., 2 pixels on each side)
            int actualComponentWidth = client.font.width(displayComponent);

            if (actualComponentWidth > maxComponentWidth) {
                displayComponent = client.font.plainSubstrByWidth(displayComponent, maxComponentWidth - client.font.width("...")) + "...";
            }

            // 4. 渲染文本 (左對齊)
            int tx = fieldX + 2; // 2 pixels padding from left
            int ty = fieldY + (fieldH - 8) / 2;

            // 錄製時使用黃色或亮色，讓玩家知道正在輸入
            int textColor = isRecording ? 0xFFFFFF55 : -1;
            context.text(client.font, displayComponent, tx, ty, textColor);
        }

        // 5. 渲染按鈕
        if (onTriggerModeChanged != null) triggerModeBtn.extractRenderState(context, mouseX, mouseY, delta);
        if (onToggleChanged != null) toggleBtn.extractRenderState(context, mouseX, mouseY, delta);
        if (currentComponent != null && !currentComponent.isEmpty()) {
            resetBtn.extractRenderState(context, mouseX, mouseY, delta);
        }
    }

    public boolean mouseClicked(MouseButtonEvent click) {
        if (currentComponent != null && !currentComponent.isEmpty()) {
            if (resetBtn.mouseClicked(click, false)) return true;
        }
        if (onTriggerModeChanged != null && triggerModeBtn.mouseClicked(click, false)) return true;
        if (onToggleChanged != null && toggleBtn.mouseClicked(click, false)) return true;

        // 只有在有綁定的情況下才處理點擊
        if (currentComponent != null && !currentComponent.isEmpty()) {
            // 如果目前正在錄製，且點擊在顯示框外面，則取消錄製
            if (isRecording && !(click.x() >= fieldX && click.x() <= fieldX + fieldW &&
                                 click.y() >= fieldY && click.y() <= fieldY + fieldH)) {
                isRecording = false;
                return false;
            }

            // 檢查是否點擊了自定義顯示框區域
            if (click.x() >= fieldX && click.x() <= fieldX + fieldW &&
                    click.y() >= fieldY && click.y() <= fieldY + fieldH) {
                if (isRecording) {
                    // 如果已經在錄製中，再次點擊顯示框則記錄滑鼠按鍵並結束錄製
                    pressedKeys.clear(); // 清除之前的按鍵
                    pressedKeys.add(click.button());
                    this.currentComponent = convertToComponent(pressedKeys);
                    isRecording = false;
                    return true;
                } else {
                    // 開始錄製
                    isRecording = true;
                    pressedKeys.clear();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean keyPressed(KeyEvent input) {
        if (isRecording) {
            int code = input.key();
            if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER || code == GLFW.GLFW_KEY_ESCAPE) {
                isRecording = false; // 這裡結束錄製後，render 就會切換回顯示 currentComponent
                return true;
            }
            pressedKeys.add(code); // 這裡加入集合
            this.currentComponent = convertToComponent(pressedKeys); // 同步更新保存用的變數
            return true;
        }
        return false;
    }

    public boolean keyReleased(KeyEvent input) {
        if (isRecording) {
            return true;
        }
        return false;
    }

    public String getValue() {
        return currentComponent;
    }
}
