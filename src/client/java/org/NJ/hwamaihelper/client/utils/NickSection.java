package org.NJ.hwamaihelper.client.utils;

public class NickSection {
    public String text;
    public String color;
    public String color2;      // 漸層色
    public String shadowColor; // 陰影色
    public String effect;      // 現在存儲如 "bold italic rainbow" 這樣的複合字串

    public NickSection(String text, String color, String effect) {
        this.text = text;
        this.color = color;
        this.color2 = "#FFFFFF";
        this.shadowColor = "#000000";
        this.effect = effect == null ? "" : effect;
    }

    // 輔助方法：檢查是否含有特定效果
    public boolean has(String e) {
        if (effect == null || effect.isEmpty()) return false;
        // 使用空格分隔判定，避免 "bold" 匹配到 "bolditalic"
        for (String s : effect.split(" ")) {
            if (s.equalsIgnoreCase(e)) return true;
        }
        return false;
    }

    // 輔助方法：切換效果 (疊加或移除)
    public void toggle(String e) {
        if (has(e)) {
            effect = effect.replace(e, "").replaceAll("\\s+", " ").trim();
        } else {
            // 色彩類效果互斥邏輯
            if (e.equals("rainbow") || e.equals("gradient") || e.equals("shadow")) {
                effect = effect.replace("rainbow", "").replace("gradient", "").replace("shadow", "");
            }
            effect = (effect + " " + e).trim();
        }
    }

    public NickSection copy() {
        NickSection copy = new NickSection(this.text, this.color, this.effect);
        copy.color2 = this.color2;
        copy.shadowColor = this.shadowColor;
        return copy;
    }

    public String toMiniMessage() {
        if (text == null || text.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        // 1. 最外層：陰影 (如果有的話)
        boolean hasShadow = has("shadow");
        if (hasShadow) {
            sb.append("<shadow:").append(shadowColor).append(">");
        }

        // 2. 中層：色彩邏輯 (彩虹 > 漸層 > 單色)
        if (has("rainbow")) {
            sb.append("<rainbow>");
        } else if (has("gradient")) {
            sb.append("<gradient:").append(color).append(":").append(color2).append(">");
        } else {
            sb.append("<").append(color).append(">");
        }

        // 3. 內層：樣式疊加 (b, i, u, st, obf)
        if (has("bold")) sb.append("<b>");
        if (has("italic")) sb.append("<i>");
        if (has("underlined")) sb.append("<u>");
        if (has("strikethrough")) sb.append("<st>");
        if (has("obfuscated")) sb.append("<obf>");

        // 4. 置入文字
        sb.append(text);

        // 5. 閉合標籤 (MiniMessage 會自動處理閉合，但手動閉合 shadow 較安全)
        if (hasShadow) sb.append("</shadow>");

        return sb.toString();
    }
}