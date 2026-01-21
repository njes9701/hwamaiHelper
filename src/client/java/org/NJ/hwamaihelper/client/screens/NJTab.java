package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput; // 確保導入

public interface NJTab {
    void init(int width, int height);
    void render(DrawContext context, int mouseX, int mouseY, float delta);
    boolean mouseClicked(Click click, boolean doubled);
    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    boolean mouseDragged(Click click, double deltaX, double deltaY);
    // 修改回使用 CharInput
    boolean charTyped(CharInput input);

    boolean keyPressed(KeyInput input);
    boolean keyReleased(KeyInput input);
    void save();
}