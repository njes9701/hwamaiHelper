package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveSlotPanel {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<ButtonWidget> saveSlotButtons = new ArrayList<>();
    private final List<ButtonWidget> deleteSlotButtons = new ArrayList<>();

    private List<NickSection> hoveredSections = null;

    public interface SaveSlotCallbacks {
        void onApplySave(List<NickSection> sections);
        void onRefreshRequest();
    }

    public void refresh(int rightPanelX, SaveSlotCallbacks callbacks) {
        saveSlotButtons.clear();
        deleteSlotButtons.clear();

        NJConfig config = NJConfigManager.getInstance();
        if (config != null && config.savedNicknames != null) {
            for (int i = 0; i < config.savedNicknames.size(); i++) {
                final int slotIndex = i;
                int slotY = 65 + (i * 25); // 稍微調大一點間距，看起來更舒服

                // 存檔套用按鈕
                ButtonWidget slotBtn = ButtonWidget.builder(Text.of("存檔 " + (slotIndex + 1)), b -> {
                    // 套用時建議進行深拷貝 (Deep Copy)，避免直接修改存檔內容
                    List<NickSection> sections = config.savedNicknames.get(slotIndex).sections;
                    callbacks.onApplySave(deepCopy(sections));
                }).dimensions(rightPanelX, slotY, 60, 20).build();

                // 刪除按鈕
                ButtonWidget delBtn = ButtonWidget.builder(Text.of("§c✕"), b -> {
                    config.savedNicknames.remove(slotIndex);
                    NJConfigManager.save();
                    callbacks.onRefreshRequest();
                }).dimensions(rightPanelX + 62, slotY, 20, 20).build();

                saveSlotButtons.add(slotBtn);
                deleteSlotButtons.add(delBtn);
            }
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta, int rightPanelX, int width, int height) {
        // 1. 繪製背景與標題
        context.fill(rightPanelX - 5, 55, width, height, 0x55000000); // 稍微加深透明背景
        context.drawTextWithShadow(client.textRenderer, "§6★ §f快速切換", rightPanelX, 45, 0xFFFFFFFF);

        // 2. 渲染按鈕與偵測懸停
        hoveredSections = null;
        NJConfig config = NJConfigManager.getInstance();

        for (int i = 0; i < saveSlotButtons.size(); i++) {
            ButtonWidget btn = saveSlotButtons.get(i);
            ButtonWidget del = deleteSlotButtons.get(i);

            btn.render(context, mouseX, mouseY, delta);
            del.render(context, mouseX, mouseY, delta);

            // 當滑鼠停留在存檔按鈕上時
            if (btn.isMouseOver(mouseX, mouseY)) {
                if (config != null && config.savedNicknames != null && i < config.savedNicknames.size()) {
                    hoveredSections = config.savedNicknames.get(i).sections;
                    // 繪製一個選取框特效
                    context.fill(btn.getX() - 2, btn.getY() - 2, del.getX() + del.getWidth() + 2, del.getY() + del.getHeight() + 2, 0x33FFFFFF);
                }
            }
        }
    }

    // 輔助方法：確保套用存檔時數據結構獨立
    private List<NickSection> deepCopy(List<NickSection> original) {
        return original.stream()
                .map(s -> new NickSection(s.text, s.color, s.effect))
                .collect(Collectors.toList());
    }

    public boolean mouseClicked(Click click, boolean d) {
        for (ButtonWidget slot : saveSlotButtons) if (slot.mouseClicked(click, d)) return true;
        for (ButtonWidget del : deleteSlotButtons) if (del.mouseClicked(click, d)) return true;
        return false;
    }

    public List<NickSection> getHoveredSections() {
        return hoveredSections;
    }
}