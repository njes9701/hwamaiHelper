package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class ConfigRow {
    public final EditBox cmdField;
    public final EditBox keyField;
    public final Button triggerModeBtn;
    public final Button toggleBtn;
    public final Button deleteBtn;
    public boolean onRelease = true;
    public boolean enabled = true;

    public ConfigRow(Font tr, String cmd, String key, boolean onRelease, boolean enabled, Runnable onDelete) {
        this.cmdField = new EditBox(tr, 0, 0, 100, 20, Component.literal("指令"));
        this.cmdField.setValue(cmd);

        this.keyField = new EditBox(tr, 0, 0, 55, 20, Component.literal("按鍵"));
        this.keyField.setValue(key);
        this.keyField.setEditable(false); // 禁止直接輸入

        this.onRelease = onRelease;
        this.triggerModeBtn = Button.builder(getTriggerModeComponent(), b -> {
            this.onRelease = !this.onRelease;
            b.setMessage(getTriggerModeComponent());
        }).bounds(0, 0, 60, 20).build();

        this.enabled = enabled;
        this.toggleBtn = Button.builder(getToggleComponent(), b -> {
            this.enabled = !this.enabled;
            b.setMessage(getToggleComponent());
        }).bounds(0, 0, 32, 20).build();

        this.deleteBtn = Button.builder(Component.literal("§c刪除"), b -> onDelete.run())
                .bounds(0, 0, 38, 20).build();
    }

    private Component getTriggerModeComponent() {
        return Component.literal(onRelease ? "§7放開觸發" : "§b按下觸發");
    }

    private Component getToggleComponent() {
        return Component.literal(enabled ? "§a開" : "§c關");
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