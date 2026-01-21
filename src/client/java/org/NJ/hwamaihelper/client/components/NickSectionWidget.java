package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.utils.ColorUtils;
import org.NJ.hwamaihelper.client.utils.NickNameConstants;
import org.NJ.hwamaihelper.client.utils.NickSection;

import java.util.ArrayList;
import java.util.List;

public class NickSectionWidget {
    private final MinecraftClient client = MinecraftClient.getInstance();
    public final TextFieldWidget textField;
    private final ButtonWidget removeBtn;
    private final List<EffectButton> effectButtons = new ArrayList<>();

    public String color;
    public String color2;
    public String shadowColor;
    public String effect;

    private static class EffectButton {
        ButtonWidget widget;
        String id;
        public EffectButton(ButtonWidget w, String id) { this.widget = w; this.id = id; }
    }

    public NickSectionWidget(MinecraftClient client, int x, int y, NickSection s, Runnable onRemove, Runnable onUpdate) {
        this.color = s.color;
        this.color2 = s.color2;
        this.shadowColor = s.shadowColor;
        this.effect = s.effect;

        this.textField = new TextFieldWidget(client.textRenderer, x, y, 80, 20, Text.of(""));
        this.textField.setText(s.text);
        this.textField.setMaxLength(32);
        // 關鍵：確保輸入框可以被選中
        this.textField.setEditable(true);

        int startX = x + 85;
        addEffectBtn(startX, y, "B", "bold", s, onUpdate);
        addEffectBtn(startX + 13, y, "I", "italic", s, onUpdate);
        addEffectBtn(startX + 26, y, "U", "underlined", s, onUpdate);
        addEffectBtn(startX + 39, y, "S", "strikethrough", s, onUpdate);
        addEffectBtn(startX + 52, y, "O", "obfuscated", s, onUpdate);

        int specialX = startX + 70;
        addEffectBtn(specialX, y, "R", "rainbow", s, onUpdate);
        addEffectBtn(specialX + 13, y, "G", "gradient", s, onUpdate);
        addEffectBtn(specialX + 26, y, "SH", "shadow", s, onUpdate);

        this.removeBtn = ButtonWidget.builder(Text.of("§c✕"), b -> onRemove.run())
                .dimensions(specialX + 45, y, 20, 20).build();
    }

    private void addEffectBtn(int x, int y, String label, String effectId, NickSection s, Runnable onUpdate) {
        ButtonWidget btn = ButtonWidget.builder(Text.of(label), b -> {
            s.toggle(effectId);
            this.effect = s.effect;
            onUpdate.run();
        }).dimensions(x, y, effectId.equals("shadow") ? 18 : 12, 20).build();

        effectButtons.add(new EffectButton(btn, effectId));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        textField.render(context, mouseX, mouseY, delta);
        removeBtn.render(context, mouseX, mouseY, delta);

        Text tooltipToDraw = null;

        for (EffectButton eb : effectButtons) {
            boolean isActive = hasEffect(eb.id);
            eb.widget.setMessage(Text.of((isActive ? "§e" : "§f") + eb.widget.getMessage().getString().replace("§e", "").replace("§f", "")));
            eb.widget.render(context, mouseX, mouseY, delta);

            if (eb.widget.isMouseOver(mouseX, mouseY)) {
                tooltipToDraw = getTooltipText(eb.id);
            }
        }

        if (removeBtn.isMouseOver(mouseX, mouseY)) {
            tooltipToDraw = Text.of("§c刪除此段文字");
        }

        if (tooltipToDraw != null) {
            context.drawTooltip(client.textRenderer, tooltipToDraw, mouseX, mouseY);
        }

        int rectX = textField.getX() - 22;
        int size = 18;
        int rectY = textField.getY() + (textField.getHeight() - size) / 2;

        if (hasEffect("gradient")) {
            int smallWidth = 9;
            drawColorBox(context, rectX, rectY, smallWidth, size, this.color);
            int colorStart = ColorUtils.hexToInt(this.color);
            int colorEnd = ColorUtils.hexToInt(this.color2);
            int midColor = ColorUtils.interpolate(colorStart, colorEnd, 0.5f);
            context.fill(rectX + smallWidth, rectY, rectX + smallWidth + 1, rectY + size, midColor | 0xFF000000);
            drawColorBox(context, rectX + 10, rectY, smallWidth, size, this.color2);
        } else if (hasEffect("shadow")) {
            int smallWidth = 9;
            drawColorBox(context, rectX, rectY, smallWidth, size, this.color);
            drawColorBox(context, rectX + 10, rectY, smallWidth, size, this.shadowColor);
        } else {
            drawColorBox(context, rectX, rectY, size, size, this.color);
        }
    }

    private Text getTooltipText(String id) {
        return switch (id) {
            case "bold" -> Text.of("§l粗體§r (B)");
            case "italic" -> Text.of("§o斜體§r (I)");
            case "underlined" -> Text.of("§n底線§r (U)");
            case "strikethrough" -> Text.of("§m刪除線§r (S)");
            case "obfuscated" -> Text.of("§kO§r 混淆 (O)");
            case "rainbow" -> Text.of("§b彩虹漸變§r (R)");
            case "gradient" -> Text.of("§6雙色漸層§r (G)");
            case "shadow" -> Text.of("§8字體陰影§r (SH)");
            default -> Text.of("");
        };
    }

    private boolean hasEffect(String e) {
        if (effect == null) return false;
        for (String s : effect.split(" ")) if (s.equalsIgnoreCase(e)) return true;
        return false;
    }

    private void drawColorBox(DrawContext context, int x, int y, int w, int h, String hex) {
        try {
            int colorInt = ColorUtils.hexToInt(hex) | 0xFF000000;
            context.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFFFF);
            context.fill(x, y, x + w, y + h, colorInt);
        } catch (Exception e) {
            context.fill(x, y, x + w, y + h, 0xFFFFFFFF);
        }
    }

    public void setY(int y) {
        this.textField.setY(y);
        this.removeBtn.setY(y);
        for (EffectButton eb : effectButtons) eb.widget.setY(y);
    }

    /**
     * 修正點：點擊邏輯需要明確處理 setFocused
     */
    public boolean mouseClicked(Click click, boolean d) {
        // 1. 如果點擊了輸入框
        if (this.textField.mouseClicked(click, d)) {
            this.textField.setFocused(true);
            return true;
        }

        // 2. 如果點擊了任何按鈕，輸入框應失去焦點
        boolean clickedBtn = false;
        if (this.removeBtn.mouseClicked(click, d)) clickedBtn = true;
        for (EffectButton eb : effectButtons) {
            if (eb.widget.mouseClicked(click, d)) clickedBtn = true;
        }

        if (clickedBtn) {
            this.textField.setFocused(false);
            return true;
        }

        // 3. 點擊色塊判定
        int rectX = textField.getX() - 22;
        int rectY = textField.getY();
        if (click.y() >= rectY && click.y() <= rectY + 20) {
            if (click.x() >= rectX && click.x() <= rectX + 20) {
                this.textField.setFocused(false);
                return false; // 返回 false 讓 Screen 處理調色盤
            }
        }

        // 4. 點擊其他空白處，取消焦點
        this.textField.setFocused(false);
        return false;
    }
}
