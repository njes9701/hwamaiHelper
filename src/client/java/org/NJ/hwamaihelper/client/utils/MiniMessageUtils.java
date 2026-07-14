package org.NJ.hwamaihelper.client.utils;

import java.util.List;

public class MiniMessageUtils {
    public static String buildFullCommand(List<NickSection> sections) {
        StringBuilder sb = new StringBuilder();
        for (NickSection section : sections) {
            sb.append(section.toMiniMessage());
        }
        return sb.toString();
    }
}
