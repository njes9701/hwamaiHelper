package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class InventoryHandler {
    private static int cooldown = 0;
    private static int lastCount = -1;

    public static void onClientTick(Minecraft client) {
        if (client.player == null) {
            lastCount = -1;
            return;
        }

        int currentCount = countFireworkRockets(client);

        if (!NJConfigManager.getInstance().autoReplenishFireworks) {
            lastCount = currentCount; // Sync state to prevent instant trigger upon enabling
            return;
        }

        if (cooldown > 0) {
            cooldown--;
        } else if (lastCount != -1) {
            // Only trigger if count is low AND it decreased (indicating usage/loss)
            if (currentCount < 5 && currentCount < lastCount) {
                boolean isDropping = client.options.keyDrop.isDown();
                boolean isUsing = client.options.keyUse.isDown();

                // Only replenish while the use key is held, not while dropping items.
                if (!isDropping && isUsing) {
                    client.player.connection.sendCommand("chmc 取得物品 煙火");
                    cooldown = 100; // 5 seconds cooldown
                }
            }
        }

        lastCount = currentCount;
    }

    private static int countFireworkRockets(Minecraft client) {
        int count = 0;
        for (int slot = 0; slot < client.player.getInventory().getContainerSize(); slot++) {
            var stack = client.player.getInventory().getItem(slot);
            if (stack.is(Items.FIREWORK_ROCKET)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
