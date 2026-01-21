package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class InventoryHandler {
    private static int cooldown = 0;
    private static int lastCount = -1;

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) {
            lastCount = -1;
            return;
        }

        int currentCount = client.player.getInventory().count(Items.FIREWORK_ROCKET);

        if (!NJConfigManager.getInstance().autoReplenishFireworks) {
            lastCount = currentCount; // Sync state to prevent instant trigger upon enabling
            return;
        }

        if (cooldown > 0) {
            cooldown--;
        } else if (lastCount != -1) {
            // Only trigger if count is low AND it decreased (indicating usage/loss)
            if (currentCount < 5 && currentCount < lastCount) {
                boolean isScreenOpen = client.currentScreen != null;
                boolean isDropping = client.options.dropKey.isPressed();
                boolean isUsing = client.options.useKey.isPressed();

                // 嚴格判斷：必須不在介面中、不是丟棄、且正在按下使用鍵(右鍵)
                if (!isScreenOpen && !isDropping && isUsing) {
                    client.player.networkHandler.sendChatCommand("chmc 取得物品 煙火");
                    cooldown = 100; // 5 seconds cooldown
                }
            }
        }

        lastCount = currentCount;
    }
}