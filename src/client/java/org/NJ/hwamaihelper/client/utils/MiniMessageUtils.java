package org.NJ.hwamaihelper.client.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.client.utils.NickNameConstants;
import java.util.List;

public class MiniMessageUtils {
    public static String buildFullCommand(List<NickSection> sections) {
        StringBuilder sb = new StringBuilder();
        for (NickSection section : sections) {
            sb.append(section.toMiniMessage());
        }
        return sb.toString();
    }

    public static Text getPreview(List<NickSection> sections) {
        MutableText preview = Text.literal("");
        for (NickSection section : sections) {
            // 關鍵修正：現在直接傳入整個 section 物件
            // 因為 NickNameConstants.getPreview(NickSection s) 會處理 text, color, color2 和 effect
            preview.append(NickNameConstants.getPreview(section));
        }
        return preview;
    }
}