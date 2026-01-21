package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigRow {
    public final TextFieldWidget cmdField;
    public final TextFieldWidget keyField;
    public final ButtonWidget deleteBtn;

    public ConfigRow(TextRenderer tr, String cmd, String key, Runnable onDelete) {
        this.cmdField = new TextFieldWidget(tr, 0, 0, 140, 20, Text.of("指令"));
        this.cmdField.setText(cmd);

        this.keyField = new TextFieldWidget(tr, 0, 0, 80, 20, Text.of("按鍵"));
        this.keyField.setText(key);
        this.keyField.setEditable(false); // 禁止直接輸入

        this.deleteBtn = ButtonWidget.builder(Text.of("§c刪除"), b -> onDelete.run())
                .dimensions(0, 0, 45, 20).build();
    }

    // 統一設定座標
    public void updatePosition(int centerX, int y) {
        this.cmdField.setX(centerX - 145);
        this.cmdField.setY(y);
        this.keyField.setX(centerX + 5);
        this.keyField.setY(y);
        this.deleteBtn.setX(centerX + 90);
        this.deleteBtn.setY(y);
    }
}