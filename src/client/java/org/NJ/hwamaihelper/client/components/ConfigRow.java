package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigRow {
    public final TextFieldWidget cmdField;
    public final TextFieldWidget keyField;
    public final ButtonWidget triggerModeBtn;
    public final ButtonWidget toggleBtn;
    public final ButtonWidget deleteBtn;
    public boolean onRelease = true;
    public boolean enabled = true;

    public ConfigRow(TextRenderer tr, String cmd, String key, boolean onRelease, boolean enabled, Runnable onDelete) {
        this.cmdField = new TextFieldWidget(tr, 0, 0, 100, 20, Text.of("指令"));
        this.cmdField.setText(cmd);

        this.keyField = new TextFieldWidget(tr, 0, 0, 55, 20, Text.of("按鍵"));
        this.keyField.setText(key);
        this.keyField.setEditable(false); // 禁止直接輸入

        this.onRelease = onRelease;
        this.triggerModeBtn = ButtonWidget.builder(getTriggerModeText(), b -> {
            this.onRelease = !this.onRelease;
            b.setMessage(getTriggerModeText());
        }).dimensions(0, 0, 60, 20).build();

        this.enabled = enabled;
        this.toggleBtn = ButtonWidget.builder(getToggleText(), b -> {
            this.enabled = !this.enabled;
            b.setMessage(getToggleText());
        }).dimensions(0, 0, 32, 20).build();

        this.deleteBtn = ButtonWidget.builder(Text.of("§c刪除"), b -> onDelete.run())
                .dimensions(0, 0, 38, 20).build();
    }

    private Text getTriggerModeText() {
        return Text.of(onRelease ? "§7放開觸發" : "§b按下觸發");
    }

    private Text getToggleText() {
        return Text.of(enabled ? "§a開" : "§c關");
    }

    // 統一設定座標
    public void updatePosition(int centerX, int y) {
        this.cmdField.setX(centerX - 145);
        this.cmdField.setY(y);
        this.keyField.setX(centerX - 40);
        this.keyField.setY(y);
        this.triggerModeBtn.setX(centerX + 20);
        this.triggerModeBtn.setY(y);
        this.toggleBtn.setX(centerX + 83);
        this.toggleBtn.setY(y);
        this.deleteBtn.setX(centerX + 118);
        this.deleteBtn.setY(y);
    }
}