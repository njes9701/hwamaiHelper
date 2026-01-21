package org.NJ.hwamaihelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class NJConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // 設定檔路徑：.minecraft/config/nj_config.json
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nj_config.json");

    private static NJConfig instance;

    public static NJConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    // 從檔案載入
    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (!file.exists()) {
            instance = new NJConfig();
            // 預設給一點範例資料
            instance.entries.add(new NJConfig.Entry("/tpaccept", "ctrl+Y"));
            instance.entries.add(new NJConfig.Entry("/spawn", "G"));
            save();
        } else {
            try (FileReader reader = new FileReader(file)) {
                instance = GSON.fromJson(reader, NJConfig.class);
            } catch (IOException e) {
                instance = new NJConfig();
                e.printStackTrace();
            }
        }
    }

    // 儲存到檔案
    public static void save() {
        if (instance == null) return;
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}