package org.NJ.hwamaihelper.client.logic;

import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import org.NJ.hwamaihelper.client.NJMainScreen;
import org.NJ.hwamaihelper.client.screens.GameModeWheelScreen;
import org.NJ.hwamaihelper.client.screens.GetItemScreen;
import org.NJ.hwamaihelper.client.screens.WorkstationScreen;
import org.NJ.hwamaihelper.config.HwamaiMalilibConfig;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;

/** MaLiLib owns all built-in and user-defined keybind dispatch. */
public final class MalilibInputHandler implements IKeybindProvider {
    private static final MalilibInputHandler INSTANCE = new MalilibInputHandler();
    private final List<ConfigHotkey> customCommandHotkeys = new ArrayList<>();
    private boolean registered;

    private MalilibInputHandler() {
    }

    public static MalilibInputHandler getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        HwamaiMalilibConfig.OPEN_MENU.getKeybind().setCallback((action, key) -> openMainScreen());
        HwamaiMalilibConfig.OPEN_WORKSTATION.getKeybind().setCallback((action, key) -> openWorkstationScreen());
        HwamaiMalilibConfig.OPEN_GET_ITEM.getKeybind().setCallback((action, key) -> openGetItemScreen());
        HwamaiMalilibConfig.GAME_MODE_WHEEL.getKeybind().setCallback((action, key) -> openGameModeWheel());
        HwamaiMalilibConfig.QUICK_LAND_LEVELING.getKeybind().setCallback((action, key) -> toggleQuickLandLeveling());
        rebuildCustomCommandHotkeys();
    }

    public void markRegistered() {
        this.registered = true;
        InputEventHandler.getKeybindManager().updateUsedKeys();
    }

    public void refreshCustomCommandHotkeys() {
        rebuildCustomCommandHotkeys();
        if (registered) {
            IKeybindManager manager = InputEventHandler.getKeybindManager();
            addHotkeys(manager);
            manager.updateUsedKeys();
        }
    }

    private void rebuildCustomCommandHotkeys() {
        customCommandHotkeys.clear();
        NJConfig config = NJConfigManager.getInstance();
        if (config == null || config.entries == null) {
            return;
        }

        int index = 0;
        for (NJConfig.Entry entry : config.entries) {
            if (entry == null || entry.command == null || entry.key == null || entry.key.isBlank()) {
                index++;
                continue;
            }

            KeybindSettings settings = entry.onRelease ? KeybindSettings.RELEASE : KeybindSettings.DEFAULT;
            ConfigHotkey hotkey = new ConfigHotkey(
                    "customCommand" + index,
                    HwamaiMalilibConfig.legacyToMalilibKey(entry.key),
                    settings,
                    entry.command,
                    "自訂指令：" + entry.command);
            hotkey.getKeybind().setCallback((action, key) -> {
                if (entry.enabled && HwamaiMalilibConfig.ENABLED.getBooleanValue()) {
                    executeCommand(entry.command);
                    return true;
                }
                return false;
            });
            customCommandHotkeys.add(hotkey);
            index++;
        }
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : allHotkeys()) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory("hwamaihelper", "HwamaiHelper", allHotkeys());
    }

    private List<IHotkey> allHotkeys() {
        List<IHotkey> hotkeys = new ArrayList<>(HwamaiMalilibConfig.HOTKEY_OPTIONS);
        hotkeys.addAll(customCommandHotkeys);
        return hotkeys;
    }

    private static boolean openMainScreen() {
        if (!isEnabled(HwamaiMalilibConfig.OPEN_MENU_ENABLED)) {
            return false;
        }
        GuiBase.openGui(new NJMainScreen(Component.literal("華麥助手")));
        return true;
    }

    private static boolean openWorkstationScreen() {
        if (!isEnabled(HwamaiMalilibConfig.OPEN_WORKSTATION_ENABLED)) {
            return false;
        }
        GuiBase.openGui(new WorkstationScreen());
        return true;
    }

    private static boolean openGetItemScreen() {
        if (!isEnabled(HwamaiMalilibConfig.OPEN_GET_ITEM_ENABLED)) {
            return false;
        }
        GuiBase.openGui(new GetItemScreen());
        return true;
    }

    private static boolean openGameModeWheel() {
        if (!isEnabled(HwamaiMalilibConfig.GAME_MODE_WHEEL_ENABLED)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        String excluded = HwamaiMalilibConfig.GAME_MODE_WHEEL_EXCLUDE_ITEM.getStringValue();
        if (minecraft.player != null && excluded != null && !excluded.isBlank()) {
            String main = BuiltInRegistries.ITEM.getKey(minecraft.player.getMainHandItem().getItem()).toString();
            String off = BuiltInRegistries.ITEM.getKey(minecraft.player.getOffhandItem().getItem()).toString();
            if (excluded.equals(main) || excluded.equals(off)) {
                return false;
            }
        }

        GuiBase.openGui(new GameModeWheelScreen());
        return true;
    }

    private static boolean toggleQuickLandLeveling() {
        if (!HwamaiMalilibConfig.ENABLED.getBooleanValue()) {
            return false;
        }
        boolean enabled = !HwamaiMalilibConfig.QUICK_LAND_LEVELING_ENABLED.getBooleanValue();
        HwamaiMalilibConfig.QUICK_LAND_LEVELING_ENABLED.setBooleanValue(enabled);
        HwamaiMalilibConfig.INSTANCE.save();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.sendOverlayMessage(Component.literal("快速整地：" + (enabled ? "已開啟" : "已關閉")));
        }
        return true;
    }

    private static boolean isEnabled(fi.dy.masa.malilib.config.options.ConfigBoolean feature) {
        return HwamaiMalilibConfig.ENABLED.getBooleanValue() && feature.getBooleanValue();
    }

    private static void executeCommand(String rawCommand) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || rawCommand == null) {
            return;
        }
        String command = rawCommand.trim();
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (!command.isBlank()) {
            minecraft.player.connection.sendCommand(command);
        }
    }
}
