package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.NJ.hwamaihelper.client.NJMainScreen;
import org.NJ.hwamaihelper.client.utils.InputUtils;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingHandler {
    private static final Map<String, Boolean> keyStateMap = new HashMap<>();

    public static void onClientTick(Minecraft client) {
        if (client.player == null) return;

        NJConfig config = NJConfigManager.getInstance();
        if (config == null) return;

        if (false) {
            updateAllKeyStates(client, config);
            return;
        }

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
                client.setScreenAndShow(new NJMainScreen(Component.literal("小助手主選單")));
                updateAllKeyStates(client, config);
                return;
            }
        }
        keyStateMap.put("MAIN_MENU", isOpeningPressed);

        String wheelKey = (config.gameModeWheelKey == null || config.gameModeWheelKey.isEmpty()) ? "alt" : config.gameModeWheelKey;
        boolean isWheelPressed = InputUtils.isBindingPressed(client, wheelKey);
        boolean wasWheelPressed = keyStateMap.getOrDefault("GAME_MODE_WHEEL", false);

        boolean isHoldingExcluded = false;
        if (config.gameModeWheelExcludeItem != null && !config.gameModeWheelExcludeItem.isEmpty()) {
            net.minecraft.world.item.ItemStack mainHand = client.player.getMainHandItem();
            net.minecraft.world.item.ItemStack offHand = client.player.getOffhandItem();

            String mainId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
            String offId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(offHand.getItem()).toString();

            if (mainId.equals(config.gameModeWheelExcludeItem) || offId.equals(config.gameModeWheelExcludeItem)) {
                isHoldingExcluded = true;
            }
        }

        if (config.gameModeWheelEnabled
                && isWheelPressed
                && !wasWheelPressed
                && !isHoldingExcluded) {
            client.setScreenAndShow(new org.NJ.hwamaihelper.client.screens.GameModeWheelScreen());
            updateAllKeyStates(client, config);
            return;
        }
        keyStateMap.put("GAME_MODE_WHEEL", isWheelPressed);

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
                client.setScreenAndShow(new org.NJ.hwamaihelper.client.screens.WorkstationScreen());
                updateAllKeyStates(client, config);
                return;
            }
        }
        keyStateMap.put("WORKSTATION_MENU", isWorkPressed);

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
                client.setScreenAndShow(new org.NJ.hwamaihelper.client.screens.GetItemScreen());
                updateAllKeyStates(client, config);
                return;
            }
        }
        keyStateMap.put("GET_ITEM_MENU", isGetItemPressed);

        String quickLandLevelingKey = config.quickLandLevelingKey == null ? "" : config.quickLandLevelingKey;
        if (!quickLandLevelingKey.isEmpty()) {
            boolean isQuickLandLevelingPressed = InputUtils.isBindingPressed(client, quickLandLevelingKey);
            boolean wasQuickLandLevelingPressed = keyStateMap.getOrDefault("QUICK_LAND_LEVELING_TOGGLE", false);

            boolean triggered = false;
            if (config.quickLandLevelingToggleOnRelease) {
                if (!isQuickLandLevelingPressed && wasQuickLandLevelingPressed) triggered = true;
            } else {
                if (isQuickLandLevelingPressed && !wasQuickLandLevelingPressed) triggered = true;
            }

            if (triggered) {
                config.quickLandLevelingEnabled = !config.quickLandLevelingEnabled;
                NJConfigManager.save();
                client.player.sendOverlayMessage(Component.literal("快速整地：" + (config.quickLandLevelingEnabled ? "已開啟" : "已關閉")));
                updateAllKeyStates(client, config);
                return;
            }

            keyStateMap.put("QUICK_LAND_LEVELING_TOGGLE", isQuickLandLevelingPressed);
        } else {
            keyStateMap.remove("QUICK_LAND_LEVELING_TOGGLE");
        }

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

    private static void updateAllKeyStates(Minecraft client, NJConfig config) {
        String openKey = (config.openMenuKey == null || config.openMenuKey.isEmpty()) ? "X + F" : config.openMenuKey;
        keyStateMap.put("MAIN_MENU", InputUtils.isBindingPressed(client, openKey));

        String wheelKey = (config.gameModeWheelKey == null || config.gameModeWheelKey.isEmpty()) ? "alt" : config.gameModeWheelKey;
        keyStateMap.put("GAME_MODE_WHEEL", InputUtils.isBindingPressed(client, wheelKey));

        String workKey = (config.openWorkstationKey == null || config.openWorkstationKey.isEmpty()) ? "shift + G" : config.openWorkstationKey;
        keyStateMap.put("WORKSTATION_MENU", InputUtils.isBindingPressed(client, workKey));

        String getItemKey = (config.openGetItemKey == null || config.openGetItemKey.isEmpty()) ? "G" : config.openGetItemKey;
        keyStateMap.put("GET_ITEM_MENU", InputUtils.isBindingPressed(client, getItemKey));

        String quickLandLevelingKey = config.quickLandLevelingKey == null ? "" : config.quickLandLevelingKey;
        if (!quickLandLevelingKey.isEmpty()) {
            keyStateMap.put("QUICK_LAND_LEVELING_TOGGLE", InputUtils.isBindingPressed(client, quickLandLevelingKey));
        } else {
            keyStateMap.remove("QUICK_LAND_LEVELING_TOGGLE");
        }

        if (config.entries != null) {
            for (NJConfig.Entry entry : config.entries) {
                keyStateMap.put(entry.key, InputUtils.isBindingPressed(client, entry.key));
            }
        }
    }

    private static void executeCommand(Minecraft client, String command) {
        if (client.player != null && !command.isEmpty()) {
            String cleanCmd = command.startsWith("/") ? command.substring(1) : command;
            client.player.connection.sendCommand(cleanCmd);
        }
    }
}
