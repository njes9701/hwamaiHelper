package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent; // 確保導入

public interface NJTab {
    void init(int width, int height);
    void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta);
    boolean mouseClicked(MouseButtonEvent click, boolean doubled);
    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY);
    // 修改回使用 CharacterEvent
    boolean charTyped(CharacterEvent input);

    boolean keyPressed(KeyEvent input);
    boolean keyReleased(KeyEvent input);
    void save();
}
