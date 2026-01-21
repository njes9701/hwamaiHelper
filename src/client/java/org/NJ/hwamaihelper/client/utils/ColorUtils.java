package org.NJ.hwamaihelper.client.utils;

import java.awt.Color;

public class ColorUtils {

    public static String hsbToHex(float h, float s, float b) {
        int rgb = Color.HSBtoRGB(h, s, b);
        return String.format("#%06X", (0xFFFFFF & rgb));
    }

    public static float[] hexToHsb(String hexStr) {
        int colorInt = hexToInt(hexStr);
        return Color.RGBtoHSB((colorInt >> 16) & 0xFF, (colorInt >> 8) & 0xFF, colorInt & 0xFF, null);
    }

    public static int hexToInt(String hex) {
        try {
            if (hex == null || hex.isEmpty()) return 0xFFFFFF;
            String cleanHex = hex.replace("#", "");

            if (cleanHex.length() == 8) {
                // 如果是 RRGGBBAA (MiniMessage 格式)
                long colorLong = Long.parseLong(cleanHex, 16);
                // 取 RGB 部分
                return (int) (colorLong >> 8) & 0xFFFFFF;
            }

            // 處理一般的 RRGGBB
            return (int)Long.parseLong(cleanHex.length() > 6 ? cleanHex.substring(0, 6) : cleanHex, 16);
        } catch (Exception e) {
            return 0xFFFFFF;
        }
    }

    public static int interpolate(int c1, int c2, float ratio) {
        int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }
}