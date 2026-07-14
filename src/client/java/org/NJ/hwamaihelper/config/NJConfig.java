package org.NJ.hwamaihelper.config;

import org.NJ.hwamaihelper.client.utils.NickSection;

import java.util.ArrayList;
import java.util.List;

public class NJConfig {
    // 基礎設定值
    public boolean enabled = true;
    public boolean autoReplenishFireworks = false;
    public boolean enableEnglishSearch = true;

    // --- 新增：開啟介面的快捷鍵設定 ---
    public String openMenuKey = "X + F";
    public String openWorkstationKey = "shift + G";
    public String openGetItemKey = "G";
    public String gameModeWheelKey = "alt";
    public String quickLandLevelingKey = "";
    
    // 每一筆功能的資料結構
    // --- 新增：功能開關設定 ---
    public boolean openMenuEnabled = true;
    public boolean openWorkstationEnabled = true;
    public boolean openGetItemEnabled = true;
    public boolean gameModeWheelEnabled = true;
    public boolean quickLandLevelingEnabled = false;
    public int quickLandLevelingTargetsPerTick = 5;
    public String gameModeWheelExcludeItem = "minecraft:stick";

    // --- 新增：觸發模式設定 ---
    public boolean openMenuOnRelease = true;
    public boolean openWorkstationOnRelease = true;
    public boolean openGetItemOnRelease = true;
    public boolean quickLandLevelingToggleOnRelease = true;

    public static class Entry {
        public String command;
        public String key;
        public boolean onRelease = true;
        public boolean enabled = true;

        public Entry(String command, String key) {
            this.command = command;
            this.key = key;
        }

        public Entry(String command, String key, boolean onRelease, boolean enabled) {
            this.command = command;
            this.key = key;
            this.onRelease = onRelease;
            this.enabled = enabled;
        }
    }

    public List<NickGroup> savedNicknames = new ArrayList<>();

    public static class NickGroup {
        public String name; // 存檔名稱，例如 "副本用", "平常穿"
        public List<NickSection> sections;

        public NickGroup(String name, List<NickSection> sections) {
            this.name = name;
            this.sections = sections;
        }
    }

    // 儲存所有自定義按鈕功能的清單
    public List<Entry> entries = new ArrayList<>();

    // 儲存玩家編輯中的暱稱區段
    public List<NickSection> nickSections = new ArrayList<>();

}
