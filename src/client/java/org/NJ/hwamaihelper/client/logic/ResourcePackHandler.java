package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.Minecraft;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.io.File;

/** Resource-pack behavior restored from the local reference project. */
public final class ResourcePackHandler {
    public static final String PACK_ID_PREFIX = "hwamai_";
    private static final File PACK_DIR = new File(
            Minecraft.getInstance().gameDirectory,
            "config/hwamaihelper/resourcepacks"
    );

    private ResourcePackHandler() {
    }

    public static void init() {
        if (!PACK_DIR.exists() && !PACK_DIR.mkdirs()) {
            System.err.println("[HwamaiHelper] Failed to create resource-pack directory: " + PACK_DIR);
        }
    }

    public static void resetCheck() {
    }

    public static void tick(Minecraft client) {
    }

    public static void setEnabled(boolean enabled) {
        NJConfig config = NJConfigManager.getInstance();
        config.resourcePackAlwaysApply = enabled;
        NJConfigManager.save();
    }

    public static File getPackDir() {
        return PACK_DIR;
    }
}
