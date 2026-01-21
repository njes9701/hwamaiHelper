package org.NJ.hwamaihelper.client.components;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ActionButtonBar {
    private ButtonWidget addSectionBtn;
    private ButtonWidget applyNickBtn;
    private ButtonWidget cancelNickBtn;
    private ButtonWidget saveCurrentBtn;

    // 定義回呼接口，讓主畫面決定點擊後要做什麼
    public interface ActionCallbacks {
        void onAdd();
        void onApply();
        void onCancel();
        void onSave();
    }

    public void init(int centerX, int y, ActionCallbacks callbacks) {
        // [+] 按鈕
        this.addSectionBtn = ButtonWidget.builder(Text.of("§a+"), b -> callbacks.onAdd())
                .dimensions(centerX + 87 , y, 20, 20).build();

        // 套用暱稱
        this.applyNickBtn = ButtonWidget.builder(Text.of("§w套用暱稱"), b -> callbacks.onApply())
                .dimensions(centerX - 82, y, 55, 20).build();

        // 取消暱稱
        this.cancelNickBtn = ButtonWidget.builder(Text.of("§w取消暱稱"), b -> callbacks.onCancel())
                .dimensions(centerX - 22, y, 55, 20).build();

        // 儲存暱稱
        this.saveCurrentBtn = ButtonWidget.builder(Text.of("§w儲存暱稱"), b -> callbacks.onSave())
                .dimensions(centerX + 37, y, 45, 20).build();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        addSectionBtn.render(context, mouseX, mouseY, delta);
        applyNickBtn.render(context, mouseX, mouseY, delta);
        cancelNickBtn.render(context, mouseX, mouseY, delta);
        saveCurrentBtn.render(context, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(Click click, boolean d) {
        return addSectionBtn.mouseClicked(click, d) ||
                applyNickBtn.mouseClicked(click, d) ||
                cancelNickBtn.mouseClicked(click, d) ||
                saveCurrentBtn.mouseClicked(click, d);
    }
}