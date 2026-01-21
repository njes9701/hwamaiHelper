package org.NJ.hwamaihelper.client.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;

public class NickNameConstants {
    public static String getEffectName(String effect) {
        return switch (effect == null ? "" : effect) {
            case "bold" -> "粗體";
            case "italic" -> "斜體";
            case "underlined" -> "底線";
            case "strikethrough" -> "刪除線";
            case "obfuscated" -> "混淆";
            case "rainbow" -> "彩虹";
            case "gradient" -> "漸層";
            case "shadow" -> "陰影";
            default -> "無";
        };
    }

    public static String getNextEffect(String current) {
        return switch (current == null ? "" : current) {
            case "" -> "bold";
            case "bold" -> "italic";
            case "italic" -> "underlined";
            case "underlined" -> "strikethrough";
            case "strikethrough" -> "obfuscated";
            case "obfuscated" -> "rainbow";
            case "rainbow" -> "gradient";
            case "gradient" -> "shadow";
            default -> "";
        };
    }

    public static MutableText getPreview(NickSection s) {
        String text = (s.text == null || s.text.isEmpty()) ? "預覽文字" : s.text;
        int len = text.length();

        // 2. 處理逐字漸層預覽
        if (s.has("gradient")) {
            MutableText gradientPreview = Text.empty();
            int colorStart = ColorUtils.hexToInt(s.color);
            int colorEnd = ColorUtils.hexToInt(s.color2);

            for (int i = 0; i < len; i++) {
                float ratio = (len > 1) ? (float) i / (len - 1) : 0f;
                int charColor = ColorUtils.interpolate(colorStart, colorEnd, ratio);
                gradientPreview.append(Text.literal(String.valueOf(text.charAt(i)))
                        .styled(st -> applyStyles(st.withColor(charColor), s)));
            }
            return gradientPreview;
        }

        // 3. 處理逐字彩虹預覽
        if (s.has("rainbow")) {
            MutableText rainbowPreview = Text.empty();
            for (int i = 0; i < len; i++) {
                float hue = (float) i / Math.max(1, len);
                int charColor = Color.HSBtoRGB(hue, 0.7f, 0.9f) & 0xFFFFFF;

                rainbowPreview.append(Text.literal(String.valueOf(text.charAt(i)))
                        .styled(st -> applyStyles(st.withColor(charColor), s)));
            }
            return rainbowPreview;
        }

        // 4. 處理一般單色效果
        return Text.literal(text).styled(style -> applyStyles(style.withColor(ColorUtils.hexToInt(s.color)), s));
    }

    public static MutableText getShadowOnly(NickSection s) {
        String text = (s.text == null || s.text.isEmpty()) ? "預覽文字" : s.text;
        // Shadow is always single color (s.shadowColor)
        return Text.literal(text).styled(style -> applyStyles(style.withColor(ColorUtils.hexToInt(s.shadowColor)), s));
    }

    public static Style applyStyles(Style style, NickSection s) {
        if (s.effect == null) return style;

        if (s.effect.contains("bold")) style = style.withBold(true);
        if (s.effect.contains("italic")) style = style.withItalic(true);
        if (s.effect.contains("underlined")) style = style.withFormatting(Formatting.UNDERLINE);
        if (s.effect.contains("strikethrough")) style = style.withFormatting(Formatting.STRIKETHROUGH);
        if (s.effect.contains("obfuscated")) style = style.withFormatting(Formatting.OBFUSCATED);

        return style;
    }
}
