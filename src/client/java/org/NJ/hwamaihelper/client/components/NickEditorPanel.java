package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import org.NJ.hwamaihelper.client.logic.NickNameManager;
import org.NJ.hwamaihelper.client.utils.NickSection;

import java.util.ArrayList;
import java.util.List;

public class NickEditorPanel {
    private final MinecraftClient client = MinecraftClient.getInstance();
    public final List<NickSectionWidget> sectionWidgets = new ArrayList<>();

    public interface ColorPickerCallback {
        void onPick(int index, int x, int y);
    }

    public void refresh(NickNameManager manager, int centerX, double scrollAmount,
                        Runnable onRefresh, ColorPickerCallback colorCallback) {
        sectionWidgets.clear();

        for (int i = 0; i < manager.sections.size(); i++) {
            final int index = i;
            NickSection s = manager.sections.get(i);
            int yPos = (int) (60 + (i * 24) - scrollAmount);

            NickSectionWidget widget = new NickSectionWidget(
                    client,
                    centerX - 95,
                    yPos,
                    s,
                    () -> {
                        manager.removeSection(index);
                        onRefresh.run();
                    },
                    onRefresh
            );

            sectionWidgets.add(widget);
        }
    }

    // 已移除 width 參數與 renderInstruction 呼叫
    public void render(DrawContext context, int mouseX, int mouseY, float delta, int height) {
        for (NickSectionWidget w : sectionWidgets) {
            // 只有在可見範圍內才渲染
            if (w.textField.getY() > 30 && w.textField.getY() < height) {
                w.render(context, mouseX, mouseY, delta);
            }
        }
    }

    public boolean mouseClicked(Click click, NickNameManager manager, ColorPickerCallback colorCallback) {
        double mx = click.x(), my = click.y();

        for (int i = 0; i < sectionWidgets.size(); i++) {
            NickSectionWidget w = sectionWidgets.get(i);
            int rectX = w.textField.getX() - 22;
            int rectY = w.textField.getY();

            if (mx >= rectX && mx <= rectX + 20 && my >= rectY && my <= rectY + 20) {
                return false;
            }

            if (w.mouseClicked(click, true)) {
                manager.activeColorIndex = i;
                return true;
            }
        }
        return false;
    }

    public void syncToManager(NickNameManager manager) {
        for (int i = 0; i < sectionWidgets.size(); i++) {
            if (i < manager.sections.size()) {
                NickSection s = manager.sections.get(i);
                NickSectionWidget w = sectionWidgets.get(i);
                s.text = w.textField.getText();
                s.color = w.color;
                s.color2 = w.color2;
                s.shadowColor = w.shadowColor;
                s.effect = w.effect;
            }
        }
    }
}