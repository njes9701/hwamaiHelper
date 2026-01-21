package org.NJ.hwamaihelper.client.logic;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.client.utils.MiniMessageUtils; // 確保有引用
import java.util.ArrayList;
import java.util.List;

public class NickNameManager {
    public final List<NickSection> sections = new ArrayList<>();
    public int activeColorIndex = 0;

    public void addSection() {
        sections.add(new NickSection("新文字", "#FFFFFF", ""));
        activeColorIndex = sections.size() - 1;
    }

    public void removeSection(int index) {
        if (sections.size() > 1) {
            sections.remove(index);
            activeColorIndex = Math.min(activeColorIndex, sections.size() - 1);
        }
    }

    public String buildCommand() {
        return MiniMessageUtils.buildFullCommand(sections);
    }

    // 修正後：直接調用 Utils 裡面的標準預覽邏輯
    public MutableText getPreviewText() {
        MutableText header = Text.literal("預覽: ").formatted(Formatting.GRAY);
        // append 會把帶樣式的 Text 接在後面
        return header.append(MiniMessageUtils.getPreview(sections));
    }

    // 修正後：存檔預覽也使用同樣的邏輯
    public MutableText getPreviewTextFromList(List<NickSection> customSections) {
        MutableText header = Text.literal("預覽: ").formatted(Formatting.GRAY);
        return header.append(MiniMessageUtils.getPreview(customSections));
    }
}