package org.NJ.hwamaihelper.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;

public class SimpleTextInputComponent {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final String label;
    private final TextFieldWidget textField;
    private final int x, y;

    private Runnable onClick;
    private boolean showItemIcon = false;

    public SimpleTextInputComponent(int x, int y, int labelWidth, int inputWidth, String label, String initialValue) {
        this.x = x;
        this.y = y;
        this.label = label;
        
        this.textField = new TextFieldWidget(client.textRenderer, x + labelWidth, y, inputWidth, 20, Text.of(label));
        this.textField.setMaxLength(128);
        this.textField.setText(initialValue != null ? initialValue : "");
    }

    public void setShowItemIcon(boolean showItemIcon) {
        this.showItemIcon = showItemIcon;
        if (showItemIcon) {
            this.textField.setEditableColor(0x00000000);
            this.textField.setUneditableColor(0x00000000);
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
        this.textField.setEditable(false);
    }

    public void setValue(String value) {
        this.textField.setText(value);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (label != null && !label.isEmpty()) {
            context.drawTextWithShadow(client.textRenderer, label, x, y + 6, -1);
        }
        
        // 渲染外框
        textField.render(context, mouseX, mouseY, delta);

        // 如果開啟物品顯示，則覆蓋文字區域繪製圖示
        if (showItemIcon) {
            String itemId = textField.getText();
            if (itemId != null && !itemId.isEmpty()) {
                try {
                    net.minecraft.item.Item item = net.minecraft.registry.Registries.ITEM.get(net.minecraft.util.Identifier.of(itemId));
                    if (item != net.minecraft.item.Items.AIR) {
                        // 在輸入框內置中繪製圖示
                        int iconX = textField.getX() + (textField.getWidth() - 16) / 2;
                        int iconY = textField.getY() + (textField.getHeight() - 16) / 2;
                        
                        // 清除原本可能的文字背景 (雖然 TextField 已經繪製了)
                        // 這裡我們直接在上面疊加即可
                        context.drawItem(new net.minecraft.item.ItemStack(item), iconX, iconY);
                        
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

    public boolean mouseClicked(Click click) {
        if (onClick != null && click.x() >= textField.getX() && click.x() <= textField.getX() + textField.getWidth()
            && click.y() >= textField.getY() && click.y() <= textField.getY() + textField.getHeight()) {
            onClick.run();
            return true;
        }
        return textField.mouseClicked(click, false);
    }

    public boolean keyPressed(KeyInput input) {
        return textField.keyPressed(input);
    }

    public boolean charTyped(CharInput input) {
        return textField.charTyped(input);
    }

    public String getValue() {
        return textField.getText();
    }
    
    public void setFocused(boolean focused) {
        textField.setFocused(focused);
    }
}
