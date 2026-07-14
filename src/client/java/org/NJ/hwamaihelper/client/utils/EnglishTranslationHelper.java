package org.NJ.hwamaihelper.client.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class EnglishTranslationHelper {
    private static final Identifier EN_US_LANG = Identifier.fromNamespaceAndPath("minecraft", "lang/en_us.json");
    private static final Map<String, String> translations = new HashMap<>();
    private static boolean loaded = false;

    public static void loadEnUs() {
        if (loaded) return;

        try (Reader reader = openEnUsReader()) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            translations.clear();

            for (String key : json.keySet()) {
                translations.put(key, json.get(key).getAsString());
            }

            loaded = !translations.isEmpty();
        } catch (Exception ignored) {
            loaded = false;
        }
    }

    private static Reader openEnUsReader() throws IOException {
        Minecraft client = Minecraft.getInstance();

        if (client != null && client.getResourceManager() != null) {
            try {
                return client.getResourceManager().openAsReader(EN_US_LANG);
            } catch (IOException ignored) {
            }
        }

        var minecraftPath = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .flatMap(container -> container.findPath("assets/minecraft/lang/en_us.json"));
        if (minecraftPath.isPresent()) {
            return Files.newBufferedReader(minecraftPath.get(), StandardCharsets.UTF_8);
        }

        var stream = EnglishTranslationHelper.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
        if (stream != null) {
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        }

        throw new IOException("Unable to load Minecraft en_us language file");
    }

    public static String translate(String key) {
        if (!loaded) {
            loadEnUs();
        }
        return translations.getOrDefault(key, key);
    }

}
