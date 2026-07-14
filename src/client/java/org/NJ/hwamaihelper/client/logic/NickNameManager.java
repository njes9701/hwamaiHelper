package org.NJ.hwamaihelper.client.logic;

import org.NJ.hwamaihelper.client.utils.NickSection;
import org.NJ.hwamaihelper.client.utils.MiniMessageUtils;

import java.util.ArrayList;
import java.util.List;

public class NickNameManager {
    public final List<NickSection> sections = new ArrayList<>();
    public void addSection() {
        sections.add(new NickSection("新文字", "#FFFFFF", ""));
    }

    public void removeSection(int index) {
        if (sections.size() > 1) {
            sections.remove(index);
        }
    }

    public String buildCommand() {
        return MiniMessageUtils.buildFullCommand(sections);
    }
}
