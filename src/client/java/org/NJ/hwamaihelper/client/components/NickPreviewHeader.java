package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.logic.NickNameManager;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.client.utils.NickNameConstants;

import java.util.List;

public class NickPreviewHeader {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public void render(DrawContext context, int width, NickNameManager manager, List<NickSection> hoverSections) {
        // 1. 繪製頂部背景
        context.fill(100, 0, width, 55, 0xFF101010);
        context.fill(100, 54, width, 55, 0x88FFFFFF);

        int centerX = (width + 100) / 2;
        List<NickSection> sectionsToRender = (hoverSections != null) ? hoverSections : manager.sections;

        // 2. 繪製狀態標籤
        String statusLabel = (hoverSections != null) ? "[ 存檔預覽 ]" : "[ 編輯中 ]";
        int labelColor = (hoverSections != null) ? 0xFF55FFFF : 0xFFFFFF55;
        context.drawTextWithShadow(client.textRenderer, statusLabel, width - 70, 10, labelColor);

        // 3. 計算總寬度以便居中
        int totalWidth = 0;
        for (NickSection s : sectionsToRender) {
            totalWidth += client.textRenderer.getWidth(s.text == null ? "" : s.text);
        }

        int startX = centerX - (totalWidth / 2);
        int currentX = startX;
        int y = 25;

        // 4. 逐段渲染 (取消手動偏移，回歸系統預設)
        for (NickSection s : sectionsToRender) {
            String content = (s.text == null || s.text.isEmpty()) ? "" : s.text;
            if (content.isEmpty()) continue;

            MutableText mainText = NickNameConstants.getPreview(s);

            if (s.has("shadow")) {
                MutableText shadowText = NickNameConstants.getShadowOnly(s);
                // 繪製自定義陰影 (右下偏移1像素)
                context.drawText(client.textRenderer, shadowText, currentX + 1, y + 1, 0xFFFFFFFF, false);
                // 繪製主文字 (不帶預設黑影)
                context.drawText(client.textRenderer, mainText, currentX, y, 0xFFFFFFFF, false);
            } else {
                // 一般模式：繪製主文字 (帶預設黑影)
                context.drawTextWithShadow(client.textRenderer, mainText, currentX, y, 0xFFFFFFFF);
            }

            currentX += client.textRenderer.getWidth(content);
        }

        // 5. 提示訊息
        if (totalWidth == 0) {
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("請在下方輸入暱稱內容").withColor(0x88AAAAAA), centerX, y, 0xFFFFFFFF);
        }
    }
}