package org.NJ.hwamaihelper.client.components;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ActionButtonBar {
    private Button addSectionBtn;
    private Button applyNickBtn;
    private Button cancelNickBtn;
    private Button saveCurrentBtn;

    // 定義回呼接口，讓主畫面決定點擊後要做什麼
    public interface ActionCallbacks {
        void onAdd();
        void onApply();
        void onCancel();
        void onSave();
    }

    public void init(int centerX, int y, ActionCallbacks callbacks) {
        // [+] 按鈕
        this.addSectionBtn = Button.builder(Component.literal("§a+"), b -> callbacks.onAdd())
                .bounds(centerX + 87 , y, 20, 20).build();

        // 套用暱稱
        this.applyNickBtn = Button.builder(Component.literal("§w套用暱稱"), b -> callbacks.onApply())
                .bounds(centerX - 82, y, 55, 20).build();

        // 取消暱稱
        this.cancelNickBtn = Button.builder(Component.literal("§w取消暱稱"), b -> callbacks.onCancel())
                .bounds(centerX - 22, y, 55, 20).build();

        // 儲存暱稱
        this.saveCurrentBtn = Button.builder(Component.literal("§w儲存暱稱"), b -> callbacks.onSave())
                .bounds(centerX + 37, y, 45, 20).build();
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        addSectionBtn.extractRenderState(context, mouseX, mouseY, delta);
        applyNickBtn.extractRenderState(context, mouseX, mouseY, delta);
        cancelNickBtn.extractRenderState(context, mouseX, mouseY, delta);
        saveCurrentBtn.extractRenderState(context, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(MouseButtonEvent click, boolean d) {
        return addSectionBtn.mouseClicked(click, d) ||
                applyNickBtn.mouseClicked(click, d) ||
                cancelNickBtn.mouseClicked(click, d) ||
                saveCurrentBtn.mouseClicked(click, d);
    }
}