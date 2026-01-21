package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.gui.DrawContext;
import org.NJ.hwamaihelper.client.utils.ColorUtils;

import java.awt.Color;

public class ColorPickerComponent {
    public int x, y;
    public final int size;
    private final int hueWidth = 10;

    public ColorPickerComponent(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void render(DrawContext context, String hex) {
        float[] hsb = ColorUtils.hexToHsb(hex);

        // 1. 繪製半透明背景底框 (增加質感)
        context.fill(x - 4, y - 4, x + size + hueWidth + 10, y + size + 4, 0xAA000000);

        // 2. 繪製色相條 (Hue Bar) - 這裡保留循環繪製，因為色相環是多色過渡
        for (int i = 0; i < size; i++) {
            context.fill(x + size + 5, y + i, x + size + 5 + hueWidth, y + i + 1,
                    0xFF000000 | Color.HSBtoRGB(i / (float) size, 1.0f, 1.0f));
        }

        // 3. 繪製「正確」的 S/B 色盤（逐列渲染，無條紋）
        float hue = hsb[0];

        for (int sy = 0; sy < size; sy++) {
            float b = 1.0f - (sy / (float) size);

            for (int sx = 0; sx < size; sx++) {
                float s = sx / (float) size;

                int rgb = Color.HSBtoRGB(hue, s, b);
                context.fill(
                        x + sx,
                        y + sy,
                        x + sx + 1,
                        y + sy + 1,
                        0xFF000000 | rgb
                );
            }
        }

        // 4. 繪製選中準星 (十字或小圓點)
        int dotX = x + Math.min(size - 1, (int)(hsb[1] * size));
        int dotY = y + Math.min(size - 1, (int)((1.0f - hsb[2]) * size));

        context.fill(dotX - 2, dotY - 2, dotX + 2, dotY + 2, 0xFFFFFFFF);
        context.fill(dotX - 1, dotY - 1, dotX + 1, dotY + 1, 0xFF000000);
    }

    public boolean isClickInHue(double mx, double my) {
        return mx >= x + size + 5 && mx <= x + size + 5 + hueWidth && my >= y && my <= y + size;
    }

    public boolean isClickInBox(double mx, double my) {
        return mx >= x && mx <= x + size && my >= y && my <= y + size;
    }

    public String pickColor(double mx, double my, String currentHex) {
        float[] hsb = ColorUtils.hexToHsb(currentHex);
        float h = hsb[0];
        float s = hsb[1];
        float b = hsb[2];

        if (isClickInHue(mx, my)) {
            h = (float)(my - y) / size;
        } else if (isClickInBox(mx, my)) {
            s = (float)(mx - x) / size;
            b = 1.0f - (float)(my - y) / size;
        }

        h = Math.max(0, Math.min(1, h));
        s = Math.max(0, Math.min(1, s));
        b = Math.max(0, Math.min(1, b));

        return ColorUtils.hsbToHex(h, s, b);
    }
}
