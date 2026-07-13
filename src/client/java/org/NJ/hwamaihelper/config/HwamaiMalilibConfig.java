package org.NJ.hwamaihelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.fabricmc.loader.api.FabricLoader;
import org.NJ.hwamaihelper.client.logic.MalilibInputHandler;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * MaLiLib-backed settings. The legacy NJConfig remains the data store for
 * structured command and nickname data, while scalar settings are mirrored
 * here so existing installations migrate without losing data.
 */
public final class HwamaiMalilibConfig implements IConfigHandler {
    public static final HwamaiMalilibConfig INSTANCE = new HwamaiMalilibConfig();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("hwamaihelper.json");
    public static final ConfigBoolean ENABLED = bool("enabled", true, "啟用華麥助手", "控制整個模組是否處理快捷鍵與自動化功能。");
    public static final ConfigBoolean AUTO_REPLENISH_FIREWORKS = bool("autoReplenishFireworks", false, "自動補充煙火", "煙火不足時自動執行補充指令。");
    public static final ConfigBoolean ENABLE_ENGLISH_SEARCH = bool("enableEnglishSearch", true, "啟用英文物品搜尋", "物品選擇器同時搜尋英文名稱。");
    public static final ConfigBoolean RESOURCE_PACK_ALWAYS_APPLY = bool("resourcePackAlwaysApply", false, "總是套用伺服器材質包", "保留原有材質包自動套用設定。");
    public static final ConfigBoolean OPEN_MENU_ENABLED = bool("openMenuEnabled", true, "啟用主選單快捷鍵", "允許以快捷鍵開啟華麥助手。");
    public static final ConfigBoolean OPEN_WORKSTATION_ENABLED = bool("openWorkstationEnabled", true, "啟用工作站快捷鍵", "允許以快捷鍵開啟工作站選單。");
    public static final ConfigBoolean OPEN_GET_ITEM_ENABLED = bool("openGetItemEnabled", true, "啟用取得物品快捷鍵", "允許以快捷鍵開啟取得物品選單。");
    public static final ConfigBoolean GAME_MODE_WHEEL_ENABLED = bool("gameModeWheelEnabled", true, "啟用遊戲模式輪盤", "允許以按住快捷鍵的方式使用模式輪盤。");
    public static final ConfigBoolean QUICK_LAND_LEVELING_ENABLED = bool("quickLandLevelingEnabled", false, "快速整地", "啟用或停用快速整地功能。");
    public static final ConfigInteger QUICK_LAND_LEVELING_TARGETS = localized(
            new ConfigInteger("quickLandLevelingTargetsPerTick", 5, 1, 10),
            "每 Tick 整地數量",
            "限制每 Tick 處理的目標數量");
    public static final ConfigString GAME_MODE_WHEEL_EXCLUDE_ITEM = localized(
            new ConfigString("gameModeWheelExcludeItem", "minecraft:stick"),
            "輪盤排除物品",
            "手持此物品時不開啟遊戲模式輪盤");

    public static final ConfigHotkey OPEN_MENU = hotkey("openMenu", "X,F", KeybindSettings.RELEASE, "開啟華麥助手", "預設 X + F");
    public static final ConfigHotkey OPEN_WORKSTATION = hotkey("openWorkstation", "LEFT_SHIFT,G", KeybindSettings.RELEASE, "開啟工作站", "預設 Shift + G");
    public static final ConfigHotkey OPEN_GET_ITEM = hotkey("openGetItem", "G", KeybindSettings.RELEASE, "開啟取得物品", "預設 G");
    public static final ConfigHotkey GAME_MODE_WHEEL = hotkey("gameModeWheel", "LEFT_ALT", KeybindSettings.DEFAULT, "遊戲模式輪盤", "按住快捷鍵並移動滑鼠選擇模式");
    public static final ConfigHotkey QUICK_LAND_LEVELING = hotkey("quickLandLeveling", "", KeybindSettings.RELEASE, "切換快速整地", "切換快速整地功能");

    public static final List<IConfigBase> GENERAL_OPTIONS = List.of(
            ENABLED,
            AUTO_REPLENISH_FIREWORKS,
            ENABLE_ENGLISH_SEARCH,
            RESOURCE_PACK_ALWAYS_APPLY,
            OPEN_MENU_ENABLED,
            OPEN_WORKSTATION_ENABLED,
            OPEN_GET_ITEM_ENABLED,
            GAME_MODE_WHEEL_ENABLED,
            QUICK_LAND_LEVELING_ENABLED,
            QUICK_LAND_LEVELING_TARGETS,
            GAME_MODE_WHEEL_EXCLUDE_ITEM
    );

    public static final List<ConfigHotkey> HOTKEY_OPTIONS = List.of(
            OPEN_MENU,
            OPEN_WORKSTATION,
            OPEN_GET_ITEM,
            GAME_MODE_WHEEL,
            QUICK_LAND_LEVELING
    );

    private HwamaiMalilibConfig() {
    }

    private static ConfigBoolean bool(String name, boolean defaultValue, String prettyName, String comment) {
        return localized(new ConfigBoolean(name, defaultValue), prettyName, comment);
    }

    private static ConfigHotkey hotkey(String name, String defaultValue, KeybindSettings settings, String prettyName, String comment) {
        return localized(new ConfigHotkey(name, defaultValue, settings), prettyName, comment);
    }

    private static <T extends IConfigBase> T localized(T config, String displayName, String comment) {
        config.setPrettyName(displayName);
        config.setTranslatedName(displayName);
        config.setComment(comment);
        return config;
    }

    @Override
    public void load() {
        NJConfig legacy = NJConfigManager.getInstance();
        importLegacy(legacy);

        if (Files.isRegularFile(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                ConfigUtils.readConfigBase(root, "General", GENERAL_OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", HOTKEY_OPTIONS);
            } catch (Exception exception) {
                System.err.println("[HwamaiHelper] Failed to load MaLiLib config: " + exception.getMessage());
            }
        } else {
            save();
        }

        syncLegacy();
    }

    @Override
    public void save() {
        syncLegacy();

        JsonObject root = new JsonObject();
        root.addProperty("configVersion", 1);
        ConfigUtils.writeConfigBase(root, "General", GENERAL_OPTIONS);
        ConfigUtils.writeConfigBase(root, "Hotkeys", HOTKEY_OPTIONS);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException exception) {
            System.err.println("[HwamaiHelper] Failed to save MaLiLib config: " + exception.getMessage());
        }
    }

    @Override
    public void onConfigsChanged() {
        save();
        MalilibInputHandler.getInstance().refreshCustomCommandHotkeys();
    }

    private static void importLegacy(NJConfig config) {
        if (config == null) {
            return;
        }

        ENABLED.setBooleanValue(config.enabled);
        AUTO_REPLENISH_FIREWORKS.setBooleanValue(config.autoReplenishFireworks);
        ENABLE_ENGLISH_SEARCH.setBooleanValue(config.enableEnglishSearch);
        RESOURCE_PACK_ALWAYS_APPLY.setBooleanValue(config.resourcePackAlwaysApply);
        OPEN_MENU_ENABLED.setBooleanValue(config.openMenuEnabled);
        OPEN_WORKSTATION_ENABLED.setBooleanValue(config.openWorkstationEnabled);
        OPEN_GET_ITEM_ENABLED.setBooleanValue(config.openGetItemEnabled);
        GAME_MODE_WHEEL_ENABLED.setBooleanValue(config.gameModeWheelEnabled);
        QUICK_LAND_LEVELING_ENABLED.setBooleanValue(config.quickLandLevelingEnabled);
        QUICK_LAND_LEVELING_TARGETS.setIntegerValue(config.quickLandLevelingTargetsPerTick);
        GAME_MODE_WHEEL_EXCLUDE_ITEM.setStringValue(config.gameModeWheelExcludeItem == null ? "minecraft:stick" : config.gameModeWheelExcludeItem);

        importHotkey(OPEN_MENU, config.openMenuKey, config.openMenuOnRelease);
        importHotkey(OPEN_WORKSTATION, config.openWorkstationKey, config.openWorkstationOnRelease);
        importHotkey(OPEN_GET_ITEM, config.openGetItemKey, config.openGetItemOnRelease);
        importHotkey(GAME_MODE_WHEEL, config.gameModeWheelKey, false);
        importHotkey(QUICK_LAND_LEVELING, config.quickLandLevelingKey, config.quickLandLevelingToggleOnRelease);
    }

    private static void importHotkey(ConfigHotkey hotkey, String value, boolean release) {
        String converted = legacyToMalilibKey(value);
        if (!converted.isEmpty()) {
            hotkey.setHotkeyStringValue(converted);
        }
        hotkey.getKeybind().setSettings(release ? KeybindSettings.RELEASE : KeybindSettings.DEFAULT);
    }

    public static void syncLegacy() {
        NJConfig config = NJConfigManager.getInstance();
        if (config == null) {
            return;
        }

        config.enabled = ENABLED.getBooleanValue();
        config.autoReplenishFireworks = AUTO_REPLENISH_FIREWORKS.getBooleanValue();
        config.enableEnglishSearch = ENABLE_ENGLISH_SEARCH.getBooleanValue();
        config.resourcePackAlwaysApply = RESOURCE_PACK_ALWAYS_APPLY.getBooleanValue();
        config.openMenuEnabled = OPEN_MENU_ENABLED.getBooleanValue();
        config.openWorkstationEnabled = OPEN_WORKSTATION_ENABLED.getBooleanValue();
        config.openGetItemEnabled = OPEN_GET_ITEM_ENABLED.getBooleanValue();
        config.gameModeWheelEnabled = GAME_MODE_WHEEL_ENABLED.getBooleanValue();
        config.quickLandLevelingEnabled = QUICK_LAND_LEVELING_ENABLED.getBooleanValue();
        config.quickLandLevelingTargetsPerTick = QUICK_LAND_LEVELING_TARGETS.getIntegerValue();
        config.gameModeWheelExcludeItem = GAME_MODE_WHEEL_EXCLUDE_ITEM.getStringValue();

        config.openMenuKey = malilibToLegacyKey(OPEN_MENU.getHotkeyStringValue());
        config.openWorkstationKey = malilibToLegacyKey(OPEN_WORKSTATION.getHotkeyStringValue());
        config.openGetItemKey = malilibToLegacyKey(OPEN_GET_ITEM.getHotkeyStringValue());
        config.gameModeWheelKey = malilibToLegacyKey(GAME_MODE_WHEEL.getHotkeyStringValue());
        config.quickLandLevelingKey = malilibToLegacyKey(QUICK_LAND_LEVELING.getHotkeyStringValue());

        config.openMenuOnRelease = isRelease(OPEN_MENU);
        config.openWorkstationOnRelease = isRelease(OPEN_WORKSTATION);
        config.openGetItemOnRelease = isRelease(OPEN_GET_ITEM);
        config.quickLandLevelingToggleOnRelease = isRelease(QUICK_LAND_LEVELING);
        NJConfigManager.save();
    }

    private static boolean isRelease(ConfigHotkey hotkey) {
        return hotkey.getKeybind().getSettings().getActivateOn() == KeyAction.RELEASE;
    }

    public static String legacyToMalilibKey(String value) {
        if (value == null || value.isBlank() || value.contains(">")) {
            return "";
        }

        List<String> result = new ArrayList<>();
        Arrays.stream(value.split("[+,]"))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(part -> normalizeKeyName(part.toUpperCase(Locale.ROOT)))
                .forEach(result::add);
        return String.join(",", result);
    }

    public static String malilibToLegacyKey(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        List<String> result = new ArrayList<>();
        Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(HwamaiMalilibConfig::toLegacyKeyName)
                .forEach(result::add);
        return String.join(" + ", result);
    }

    private static String normalizeKeyName(String key) {
        return switch (key) {
            case "CTRL", "CONTROL", "LEFT_CTRL" -> "LEFT_CONTROL";
            case "RIGHT_CTRL" -> "RIGHT_CONTROL";
            case "SHIFT" -> "LEFT_SHIFT";
            case "ALT" -> "LEFT_ALT";
            case "MOUSE_LEFT" -> "BUTTON_1";
            case "MOUSE_RIGHT" -> "BUTTON_2";
            case "MOUSE_MIDDLE" -> "BUTTON_3";
            default -> key.startsWith("NUMPAD_") ? "KP_" + key.substring("NUMPAD_".length()) : key;
        };
    }

    private static String toLegacyKeyName(String key) {
        return switch (key.toUpperCase(Locale.ROOT)) {
            case "LEFT_CONTROL" -> "ctrl";
            case "RIGHT_CONTROL" -> "right_ctrl";
            case "LEFT_SHIFT" -> "shift";
            case "RIGHT_SHIFT" -> "right_shift";
            case "LEFT_ALT" -> "alt";
            case "RIGHT_ALT" -> "right_alt";
            case "BUTTON_1" -> "mouse_left";
            case "BUTTON_2" -> "mouse_right";
            case "BUTTON_3" -> "mouse_middle";
            default -> key.startsWith("KP_") ? "numpad_" + key.substring(3).toLowerCase(Locale.ROOT) : key;
        };
    }
}
