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
        if (client.player == null || client.currentScreen != null) return;

        NJConfig config = NJConfigManager.getInstance();
        if (config == null) return;

        // --- 修正 1：動態偵測開啟介面的快捷鍵 ---
        String openKey = (config.openMenuKey == null || config.openMenuKey.isEmpty()) ? "X + F" : config.openMenuKey;
        boolean isOpeningPressed = InputUtils.isBindingPressed(client, openKey);

        if (isOpeningPressed) {
            // 使用 MAIN_MENU 標記防止長按重複開啟
            if (!keyStateMap.getOrDefault("MAIN_MENU", false)) {
                client.setScreen(new NJMainScreen(Text.of("華麥助手設定")));
                keyStateMap.put("MAIN_MENU", true);
            }
        } else {
            keyStateMap.put("MAIN_MENU", false);
        }

        // --- 新增：工作方塊介面快捷鍵 ---
        String workKey = (config.openWorkstationKey == null || config.openWorkstationKey.isEmpty()) ? "shift + G" : config.openWorkstationKey;
        boolean isWorkPressed = InputUtils.isBindingPressed(client, workKey);

        if (isWorkPressed) {
            if (!keyStateMap.getOrDefault("WORKSTATION_MENU", false)) {
                client.setScreen(new org.NJ.hwamaihelper.client.screens.WorkstationScreen());
                keyStateMap.put("WORKSTATION_MENU", true);
            }
        } else {
            keyStateMap.put("WORKSTATION_MENU", false);
        }

        // --- 新增：取得物品介面快捷鍵 ---
        String getItemKey = (config.openGetItemKey == null || config.openGetItemKey.isEmpty()) ? "G" : config.openGetItemKey;
        boolean isGetItemPressed = InputUtils.isBindingPressed(client, getItemKey);

        if (isGetItemPressed) {
            if (!keyStateMap.getOrDefault("GET_ITEM_MENU", false)) {
                client.setScreen(new org.NJ.hwamaihelper.client.screens.GetItemScreen());
                keyStateMap.put("GET_ITEM_MENU", true);
            }
        } else {
            keyStateMap.put("GET_ITEM_MENU", false);
        }

        // --- 修正 2：偵測自定義指令快捷鍵 ---
        if (config.entries != null) {
            for (NJConfig.Entry entry : config.entries) {
                boolean isPressed = InputUtils.isBindingPressed(client, entry.key);
                if (isPressed) {
                    if (!keyStateMap.getOrDefault(entry.key, false)) {
                        executeCommand(client, entry.command);
                        keyStateMap.put(entry.key, true);
                    }
                } else {
                    keyStateMap.put(entry.key, false);
                }
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
