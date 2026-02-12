package org.NJ.hwamaihelper.mixin.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.NJ.hwamaihelper.client.utils.EnglishTranslationHelper;
import org.NJ.hwamaihelper.config.NJConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onGetTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (!NJConfigManager.getInstance().enableEnglishSearch) {
            return;
        }

        List<Text> tooltip = cir.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        String translationKey = stack.getItem().getTranslationKey();

        if (EnglishTranslationHelper.hasTranslation(translationKey)) {
            String englishName = EnglishTranslationHelper.translate(translationKey);
            
            // Check if the current language is already English to avoid duplication
            // However, it's safer to just check if the tooltip already contains this text.
            boolean alreadyHas = false;
            for (Text line : tooltip) {
                if (line.getString().equalsIgnoreCase(englishName)) {
                    alreadyHas = true;
                    break;
                }
            }
            
            if (!alreadyHas) {
                // Add English name in dark gray to the tooltip.
                // This makes it searchable in the creative inventory.
                tooltip.add(Text.literal(englishName).formatted(Formatting.DARK_GRAY));
            }
        }
    }
}
