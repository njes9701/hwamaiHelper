package org.NJ.hwamaihelper.client.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnglishTranslationHelper {
    private static final Map<String, String> translations = new HashMap<>();
    private static boolean loaded = false;

    public static void loadEnUs() {
        if (loaded) return;
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Optional<Resource> resource = client.getResourceManager().getResource(Identifier.of("minecraft", "lang/en_us.json"));
            
            if (resource.isPresent()) {
                try (Reader reader = new InputStreamReader(resource.get().getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                    for (String key : json.keySet()) {
                        translations.put(key, json.get(key).getAsString());
                    }
                    loaded = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String translate(String key) {
        if (!loaded) {
            loadEnUs();
        }
        return translations.getOrDefault(key, key);
    }
    
    public static boolean hasTranslation(String key) {
        if (!loaded) {
            loadEnUs();
        }
        return translations.containsKey(key);
    }
}
