package org.NJ.hwamaihelper.client.utils;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.awt.Color;

public class NickNameConstants {
    public static MutableComponent getPreview(NickSection s) {
        String text = (s.text == null || s.text.isEmpty()) ? "預覽文字" : s.text;
        int len = text.length();

        // 2. 處理逐字漸層預覽
        if (s.has("gradient")) {
            MutableComponent gradientPreview = Component.empty();
            int colorStart = ColorUtils.hexToInt(s.color);
            int colorEnd = ColorUtils.hexToInt(s.color2);

            for (int i = 0; i < len; i++) {
                float ratio = (len > 1) ? (float) i / (len - 1) : 0f;
                int charColor = ColorUtils.interpolate(colorStart, colorEnd, ratio);
                gradientPreview.append(Component.literal(String.valueOf(text.charAt(i)))
                        .withStyle(st -> applyStyles(st.withColor(charColor), s)));
            }
            return gradientPreview;
        }

        // 3. 處理逐字彩虹預覽
        if (s.has("rainbow")) {
            MutableComponent rainbowPreview = Component.empty();
            for (int i = 0; i < len; i++) {
                float hue = (float) i / Math.max(1, len);
                int charColor = Color.HSBtoRGB(hue, 0.7f, 0.9f) & 0xFFFFFF;

                rainbowPreview.append(Component.literal(String.valueOf(text.charAt(i)))
                        .withStyle(st -> applyStyles(st.withColor(charColor), s)));
            }
            return rainbowPreview;
        }

        // 4. 處理一般單色效果
        return Component.literal(text).withStyle(style -> applyStyles(style.withColor(ColorUtils.hexToInt(s.color)), s));
    }

    public static MutableComponent getShadowOnly(NickSection s) {
        String text = (s.text == null || s.text.isEmpty()) ? "預覽文字" : s.text;
        // Shadow is always single color (s.shadowColor)
        return Component.literal(text).withStyle(style -> applyStyles(style.withColor(ColorUtils.hexToInt(s.shadowColor)), s));
    }

    public static Style applyStyles(Style style, NickSection s) {
        if (s.effect == null) return style;

        if (s.effect.contains("bold")) style = style.withBold(true);
        if (s.effect.contains("italic")) style = style.withItalic(true);
        if (s.effect.contains("underlined")) style = style.withUnderlined(true);
        if (s.effect.contains("strikethrough")) style = style.withStrikethrough(true);
        if (s.effect.contains("obfuscated")) style = style.withObfuscated(true);

        return style;
    }
}
