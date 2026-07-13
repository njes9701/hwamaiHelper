package org.NJ.hwamaihelper.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.NJ.hwamaihelper.client.utils.EnglishTranslationHelper;
import org.NJ.hwamaihelper.config.NJConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {
    @Shadow
    private EditBox searchBox;

    @Inject(method = "refreshSearchResults", at = @At("TAIL"))
    private void appendEnglishSearchResults(CallbackInfo ci) {
        if (!NJConfigManager.getInstance().enableEnglishSearch) {
            return;
        }

        String query = normalizeSearchText(searchBox.getValue());
        if (query.isEmpty() || query.startsWith("#")) {
            return;
        }

        CreativeModeInventoryScreen.ItemPickerMenu menu =
                ((CreativeModeInventoryScreen) (Object) this).getMenu();
        Set<Item> existingItems = new HashSet<>();
        for (ItemStack stack : menu.items) {
            existingItems.add(stack.getItem());
        }

        boolean addedResult = false;
        for (ItemStack stack : CreativeModeTabs.searchTab().getDisplayItems()) {
            Item item = stack.getItem();
            if (existingItems.contains(item)) {
                continue;
            }

            String englishName = EnglishTranslationHelper.translate(item.getDescriptionId());
            if (normalizeSearchText(englishName).contains(query)) {
                menu.items.add(stack);
                existingItems.add(item);
                addedResult = true;
            }
        }

        if (addedResult) {
            menu.scrollTo(0.0F);
        }
    }

    private static String normalizeSearchText(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
    }
}
