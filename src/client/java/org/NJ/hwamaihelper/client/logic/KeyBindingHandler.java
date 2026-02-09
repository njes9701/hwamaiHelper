package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.NJMainScreen;
import org.NJ.hwamaihelper.client.utils.InputUtils;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingHandler {
    private static final Map<String, Boolean> keyStateMap = new HashMap<>();

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        NJConfig config = NJConfigManager.getInstance();
        if (config == null) return;

        // 如果目前有開啟其他介面，我們只更新按鍵狀態而不執行任何動作
        // 這樣可以防止關閉介面後觸發「放開按鍵」的邏輯
        if (client.currentScreen != null) {
            updateAllKeyStates(client, config);
            return;
        }

        // --- 1. 小助手主開關 (最高優先權) ---
        String openKey = (config.openMenuKey == null || config.openMenuKey.isEmpty()) ? "X + F" : config.openMenuKey;
        boolean isOpeningPressed = InputUtils.isBindingPressed(client, openKey);
        boolean wasOpeningPressed = keyStateMap.getOrDefault("MAIN_MENU", false);

        if (config.openMenuEnabled) {
            boolean triggered = false;
            if (config.openMenuOnRelease) {
                if (!isOpeningPressed && wasOpeningPressed) triggered = true;
            } else {
                if (isOpeningPressed && !wasOpeningPressed) triggered = true;
            }
            if (triggered) {
                client.setScreen(new NJMainScreen(Text.of("華麥助手設定")));
                updateAllKeyStates(client, config); // 更新所有狀態防止同一跳觸發其他按鍵
                return;
            }
        }
        keyStateMap.put("MAIN_MENU", isOpeningPressed);

        // --- 2. 遊戲模式切換轉盤 ---
        String wheelKey = (config.gameModeWheelKey == null || config.gameModeWheelKey.isEmpty()) ? "alt" : config.gameModeWheelKey;
        boolean isWheelPressed = InputUtils.isBindingPressed(client, wheelKey);
        if (config.gameModeWheelEnabled && isWheelPressed) {
             client.setScreen(new org.NJ.hwamaihelper.client.screens.GameModeWheelScreen());
             updateAllKeyStates(client, config);
             return;
        }
        keyStateMap.put("GAME_MODE_WHEEL", isWheelPressed);

        // --- 3. 工作方塊介面快捷鍵 ---
        String workKey = (config.openWorkstationKey == null || config.openWorkstationKey.isEmpty()) ? "shift + G" : config.openWorkstationKey;
        boolean isWorkPressed = InputUtils.isBindingPressed(client, workKey);
        boolean wasWorkPressed = keyStateMap.getOrDefault("WORKSTATION_MENU", false);

        if (config.openWorkstationEnabled) {
            boolean triggered = false;
            if (config.openWorkstationOnRelease) {
                if (!isWorkPressed && wasWorkPressed) triggered = true;
            } else {
                if (isWorkPressed && !wasWorkPressed) triggered = true;
            }
            if (triggered) {
                client.setScreen(new org.NJ.hwamaihelper.client.screens.WorkstationScreen());
                updateAllKeyStates(client, config);
                return;
            }
        }
        keyStateMap.put("WORKSTATION_MENU", isWorkPressed);

        // --- 4. 取得物品介面快捷鍵 ---
        String getItemKey = (config.openGetItemKey == null || config.openGetItemKey.isEmpty()) ? "G" : config.openGetItemKey;
        boolean isGetItemPressed = InputUtils.isBindingPressed(client, getItemKey);
        boolean wasGetItemPressed = keyStateMap.getOrDefault("GET_ITEM_MENU", false);

        if (config.openGetItemEnabled) {
            boolean triggered = false;
            if (config.openGetItemOnRelease) {
                if (!isGetItemPressed && wasGetItemPressed) triggered = true;
            } else {
                if (isGetItemPressed && !wasGetItemPressed) triggered = true;
            }
            if (triggered) {
                client.setScreen(new org.NJ.hwamaihelper.client.screens.GetItemScreen());
                updateAllKeyStates(client, config);
                return;
            }
        }
        keyStateMap.put("GET_ITEM_MENU", isGetItemPressed);

        // --- 5. 偵測自定義指令快捷鍵 ---
        if (config.entries != null) {
            for (NJConfig.Entry entry : config.entries) {
                boolean isPressed = InputUtils.isBindingPressed(client, entry.key);
                boolean wasPressed = keyStateMap.getOrDefault(entry.key, false);
                
                if (entry.enabled) {
                    boolean triggered = false;
                    if (entry.onRelease) {
                        if (!isPressed && wasPressed) triggered = true;
                    } else {
                        if (isPressed && !wasPressed) triggered = true;
                    }
                    if (triggered) {
                        executeCommand(client, entry.command);
                        updateAllKeyStates(client, config);
                        return;
                    }
                }
                keyStateMap.put(entry.key, isPressed);
            }
        }
    }

    /**
     * 在觸發任一功能後，同步更新所有按鍵的當前狀態到 keyStateMap，
     * 防止在同一 Tick 內或緊接著的 Tick 內觸發其他衝突的按鍵邏輯。
     */
    private static void updateAllKeyStates(MinecraftClient client, NJConfig config) {
        String openKey = (config.openMenuKey == null || config.openMenuKey.isEmpty()) ? "X + F" : config.openMenuKey;
        keyStateMap.put("MAIN_MENU", InputUtils.isBindingPressed(client, openKey));

        String wheelKey = (config.gameModeWheelKey == null || config.gameModeWheelKey.isEmpty()) ? "alt" : config.gameModeWheelKey;
        keyStateMap.put("GAME_MODE_WHEEL", InputUtils.isBindingPressed(client, wheelKey));

        String workKey = (config.openWorkstationKey == null || config.openWorkstationKey.isEmpty()) ? "shift + G" : config.openWorkstationKey;
        keyStateMap.put("WORKSTATION_MENU", InputUtils.isBindingPressed(client, workKey));

        String getItemKey = (config.openGetItemKey == null || config.openGetItemKey.isEmpty()) ? "G" : config.openGetItemKey;
        keyStateMap.put("GET_ITEM_MENU", InputUtils.isBindingPressed(client, getItemKey));

        if (config.entries != null) {
            for (NJConfig.Entry entry : config.entries) {
                keyStateMap.put(entry.key, InputUtils.isBindingPressed(client, entry.key));
            }
        }
    }

    private static void executeCommand(MinecraftClient client, String command) {
        if (client.player != null && !command.isEmpty()) {
            String cleanCmd = command.startsWith("/") ? command.substring(1) : command;
            client.player.networkHandler.sendChatCommand(cleanCmd);
        }
    }
}
