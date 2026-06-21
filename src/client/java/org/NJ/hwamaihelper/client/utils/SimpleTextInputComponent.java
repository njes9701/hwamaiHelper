package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;

public class SimpleTextInputComponent {
    private final Minecraft client = Minecraft.getInstance();
    private final String label;
    private final EditBox textField;
    private final int x, y;

    private Runnable onMouseButtonEvent;
    private boolean showItemIcon = false;

    public SimpleTextInputComponent(int x, int y, int labelWidth, int inputWidth, String label, String initialValue) {
        this.x = x;
        this.y = y;
        this.label = label;
        
        this.textField = new EditBox(client.font, x + labelWidth, y, inputWidth, 20, Component.literal(label));
        this.textField.setEditable(true);
        this.textField.setMaxLength(128);
        this.textField.setValue(initialValue != null ? initialValue : "");
    }

    public void setShowItemIcon(boolean showItemIcon) {
        this.showItemIcon = showItemIcon;
        if (showItemIcon) {
            this.textField.setTextColor(0x00000000);
            this.textField.setTextColorUneditable(0x00000000);
        }
    }

    public void setOnMouseButtonEvent(Runnable onMouseButtonEvent) {
        this.onMouseButtonEvent = onMouseButtonEvent;
        this.textField.setEditable(false);
    }

    public void setValue(String value) {
        this.textField.setValue(value);
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (label != null && !label.isEmpty()) {
            context.text(client.font, label, x, y + 6, -1);
        }
        
        // 渲染外框
        textField.extractRenderState(context, mouseX, mouseY, delta);

        // 如果開啟物品顯示，則覆蓋文字區域繪製圖示
        if (showItemIcon) {
            String itemId = textField.getValue();
            if (itemId != null && !itemId.isEmpty()) {
                try {
                    net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(itemId));
                    if (item != net.minecraft.world.item.Items.AIR) {
                        // 在輸入框內置中繪製圖示
                        int iconX = textField.getX() + (textField.getWidth() - 16) / 2;
                        int iconY = textField.getY() + (textField.getHeight() - 16) / 2;
                        
                        // 清除原本可能的文字背景 (雖然 ComponentField 已經繪製了)
                        // 這裡我們直接在上面疊加即可
                        context.item(new net.minecraft.world.item.ItemStack(item), iconX, iconY);
                        
                        // 如果滑鼠懸停，顯示物品名稱
                        if (mouseX >= textField.getX() && mouseX <= textField.getX() + textField.getWidth() &&
                            mouseY >= textField.getY() && mouseY <= textField.getY() + textField.getHeight()) {
                            // 延遲渲染 Tooltip 通常由 Screen 處理，但在這裡我們可以直接繪製
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public boolean mouseClicked(MouseButtonEvent click) {
        boolean inside = click.x() >= textField.getX() && click.x() <= textField.getX() + textField.getWidth()
                && click.y() >= textField.getY() && click.y() <= textField.getY() + textField.getHeight();

        if (onMouseButtonEvent != null && inside) {
            onMouseButtonEvent.run();
            return true;
        }

        if (inside) {
            textField.setFocused(true);
            textField.mouseClicked(click, false);
            return true;
        }

        textField.setFocused(false);
        return false;
    }

    public boolean keyPressed(KeyEvent input) {
        return textField.keyPressed(input);
    }

    public boolean charTyped(CharacterEvent input) {
        return textField.charTyped(input);
    }

    public String getValue() {
        return textField.getValue();
    }
    
    public void setFocused(boolean focused) {
        textField.setFocused(focused);
    }
}
