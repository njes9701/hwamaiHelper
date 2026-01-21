package org.NJ.hwamaihelper.config;

import org.NJ.hwamaihelper.client.utils.NickSection;

import java.util.ArrayList;
import java.util.List;

public class NJConfig {
    // 基礎設定值
    public boolean autoDisableResourcePack = false;
    public int chunkLoadDistance = 10;
    public boolean enabled = true;
    public boolean autoReplenishFireworks = false;

    // --- 新增：開啟介面的快捷鍵設定 ---
    public String openMenuKey = "X + F";
    public String openWorkstationKey = "shift + G";
    public String openGetItemKey = "G";
    public String gameModeWheelKey = "alt";
    
    // 紀錄上次開啟的分頁索引 (0: 指令, 1: 設定, 2: 暱稱)
    public int lastTabIndex = 0;

    // 每一筆功能的資料結構
    public static class Entry {
        public String command;
        public String key;

        public Entry(String command, String key) {
            this.command = command;
            this.key = key;
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

    // --- 狀態緩存區 (用於判斷是否需要重複執行指令) ---

    // 紀錄上一次材質包的開關狀態
    public boolean lastAutoDisableStatus = false;

    // 紀錄上一次執行的強載入距離
    public int lastChunkDistance = -1;

    // 重要：紀錄進入伺服器後是否已經完成「首次同步」
    public boolean hasInitialized = false;

    // 紀錄材質包是否已在本次啟動後初始化
    public boolean hasInitializedPack = false;

    // 暱稱儲存相關
    public String lastSavedNick = "";
    public String selectedColorHex = "#FFFFFF";
    public String lastSelectedEffect = "";
}