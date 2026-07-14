package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.NJ.hwamaihelper.client.utils.ColorUtils;
import org.NJ.hwamaihelper.client.utils.NickNameConstants;
import org.NJ.hwamaihelper.client.utils.NickSection;

import java.util.ArrayList;
import java.util.List;

public class NickSectionWidget {
    private final Minecraft client = Minecraft.getInstance();
    public final EditBox textField;
    private final Button removeBtn;
    private final List<EffectButton> effectButtons = new ArrayList<>();

    public String color;
    public String color2;
    public String shadowColor;
    public String effect;

    private static class EffectButton {
        Button widget;
        String id;
        public EffectButton(Button w, String id) { this.widget = w; this.id = id; }
    }

    public NickSectionWidget(Minecraft client, int x, int y, NickSection s, Runnable onRemove, Runnable onUpdate) {
        this.color = s.color;
        this.color2 = s.color2;
        this.shadowColor = s.shadowColor;
        this.effect = s.effect;

        this.textField = new EditBox(client.font, x, y, 80, 20, Component.literal(""));
        this.textField.setValue(s.text);
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

        this.removeBtn = Button.builder(Component.literal("§c✕"), b -> onRemove.run())
                .bounds(specialX + 45, y, 20, 20).build();
    }

    private void addEffectBtn(int x, int y, String label, String effectId, NickSection s, Runnable onUpdate) {
        Button btn = Button.builder(Component.literal(label), b -> {
            s.toggle(effectId);
            this.effect = s.effect;
            onUpdate.run();
        }).bounds(x, y, effectId.equals("shadow") ? 18 : 12, 20).build();

        effectButtons.add(new EffectButton(btn, effectId));
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        textField.extractRenderState(context, mouseX, mouseY, delta);
        removeBtn.extractRenderState(context, mouseX, mouseY, delta);

        Component tooltipToDraw = null;

        for (EffectButton eb : effectButtons) {
            boolean isActive = hasEffect(eb.id);
            eb.widget.setMessage(Component.literal((isActive ? "§e" : "§f") + eb.widget.getMessage().getString().replace("§e", "").replace("§f", "")));
            eb.widget.extractRenderState(context, mouseX, mouseY, delta);

            if (eb.widget.isMouseOver(mouseX, mouseY)) {
                tooltipToDraw = getTooltipComponent(eb.id);
            }
        }

        if (removeBtn.isMouseOver(mouseX, mouseY)) {
            tooltipToDraw = Component.literal("§c刪除此段文字");
        }

        if (tooltipToDraw != null) {
            context.setTooltipForNextFrame(client.font, tooltipToDraw, mouseX, mouseY);
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

    private Component getTooltipComponent(String id) {
        return switch (id) {
            case "bold" -> Component.literal("§l粗體§r (B)");
            case "italic" -> Component.literal("§o斜體§r (I)");
            case "underlined" -> Component.literal("§n底線§r (U)");
            case "strikethrough" -> Component.literal("§m刪除線§r (S)");
            case "obfuscated" -> Component.literal("§kO§r 混淆 (O)");
            case "rainbow" -> Component.literal("§b彩虹漸變§r (R)");
            case "gradient" -> Component.literal("§6雙色漸層§r (G)");
            case "shadow" -> Component.literal("§8字體陰影§r (SH)");
            default -> Component.literal("");
        };
    }

    private boolean hasEffect(String e) {
        if (effect == null) return false;
        for (String s : effect.split(" ")) if (s.equalsIgnoreCase(e)) return true;
        return false;
    }

    private void drawColorBox(GuiGraphicsExtractor context, int x, int y, int w, int h, String hex) {
        try {
            int colorInt = ColorUtils.hexToInt(hex) | 0xFF000000;
            context.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFFFF);
            context.fill(x, y, x + w, y + h, colorInt);
        } catch (Exception e) {
            context.fill(x, y, x + w, y + h, 0xFFFFFFFF);
        }
    }

    /**
     * 修正點：點擊邏輯需要明確處理 setFocused
     */
    public boolean mouseClicked(MouseButtonEvent click, boolean d) {
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
