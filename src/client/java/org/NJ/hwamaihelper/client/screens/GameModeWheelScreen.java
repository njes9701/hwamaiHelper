package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.utils.InputUtils;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class GameModeWheelScreen extends Screen {

    private int selectedIndex = -1; // 0: Creative, 1: Spectator, 2: Survival

    public GameModeWheelScreen() {
        super(Text.of("Game Mode Wheel"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int radius = 65;

        // 計算滑鼠角度與距離
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.atan2(dy, dx);
        double degrees = Math.toDegrees(angle);

        // 判定選取範圍
        if (distance > 10) {
            if (degrees >= -150 && degrees < -30) {
                selectedIndex = 0; // Top
            } else if (degrees >= -30 && degrees < 90) {
                selectedIndex = 1; // Right-Down
            } else {
                selectedIndex = 2; // Left-Down
            }
        } else {
            selectedIndex = -1;
        }

        // 1. 繪製圓形背景
        drawCircle(context, centerX, centerY, radius, 0xAA000000);

        // 2. 繪製白色邊框
        drawCircleOutline(context, centerX, centerY, radius, 0xFFFFFFFF);

        // 3. 繪製選中區域的高亮
        if (selectedIndex != -1) {
            drawSectorHighlight(context, centerX, centerY, radius, selectedIndex);
        }

        // 4. 繪製分割線
        drawDivider(context, centerX, centerY, radius, -30);
        drawDivider(context, centerX, centerY, radius, 90);
        drawDivider(context, centerX, centerY, radius, 210);

        // 5. 繪製模式圖示與文字
        int iconDist = 38;
        
        drawMode(context, centerX, centerY - iconDist, new ItemStack(Items.GRASS_BLOCK), "創造", selectedIndex == 0);
        
        int specX = centerX + (int)(iconDist * Math.cos(Math.toRadians(30)));
        int specY = centerY + (int)(iconDist * Math.sin(Math.toRadians(30)));
        drawMode(context, specX, specY, new ItemStack(Items.ENDER_EYE), "觀察者", selectedIndex == 1);

        int survX = centerX + (int)(iconDist * Math.cos(Math.toRadians(150)));
        int survY = centerY + (int)(iconDist * Math.sin(Math.toRadians(150)));
        drawMode(context, survX, survY, new ItemStack(Items.DIAMOND_SWORD), "生存", selectedIndex == 2);
    }

    private void drawCircle(DrawContext context, int cx, int cy, int r, int color) {
        for (int i = -r; i <= r; i++) {
            int w = (int) Math.sqrt(r * r - i * i);
            context.fill(cx - w, cy + i, cx + w, cy + i + 1, color);
        }
    }

    private void drawCircleOutline(DrawContext context, int cx, int cy, int r, int color) {
        for (double theta = 0; theta < 2 * Math.PI; theta += 0.01) {
            int x = cx + (int) (r * Math.cos(theta));
            int y = cy + (int) (r * Math.sin(theta));
            context.fill(x, y, x + 1, y + 1, color);
        }
    }

    private void drawSectorHighlight(DrawContext context, int cx, int cy, int r, int index) {
        int highlightColor = 0x22FFFFFF;
        int iconDist = 38;
        int hX = cx, hY = cy;
        
        if (index == 0) hY -= iconDist;
        else if (index == 1) {
            hX += (int)(iconDist * Math.cos(Math.toRadians(30)));
            hY += (int)(iconDist * Math.sin(Math.toRadians(30)));
        } else {
            hX += (int)(iconDist * Math.cos(Math.toRadians(150)));
            hY += (int)(iconDist * Math.sin(Math.toRadians(150)));
        }
        drawCircle(context, hX, hY, 25, highlightColor);
    }

    private void drawDivider(DrawContext context, int cx, int cy, int r, double deg) {
        double rad = Math.toRadians(deg);
        for (int i = 0; i < r; i++) {
            int x = cx + (int)(i * Math.cos(rad));
            int y = cy + (int)(i * Math.sin(rad));
            context.fill(x, y, x + 1, y + 1, 0x33FFFFFF);
        }
    }

    private void drawMode(DrawContext context, int x, int y, ItemStack stack, String label, boolean selected) {
        context.drawItem(stack, x - 8, y - 12);
        int color = selected ? 0xFFFFFF : 0xBBBBBB;
        context.drawCenteredTextWithShadow(textRenderer, label, x, y + 6, color);
    }

    @Override
    public void tick() {
        super.tick();
        String key = NJConfigManager.getInstance().gameModeWheelKey;
        if (key == null || key.isEmpty()) key = "alt";
        if (!InputUtils.isBindingPressed(client, key)) {
            executeSelection();
            this.close();
        }
    }

    private void executeSelection() {
        if (selectedIndex != -1 && client.player != null) {
            boolean isChungHwa = org.NJ.hwamaihelper.client.utils.ServerUtils.isChungHwaServer(client);

            String cmd;
            if (isChungHwa) {
                // 華麥伺服器專用簡短指令
                cmd = switch(selectedIndex) {
                    case 0 -> "gm c";
                    case 1 -> "gm sp";
                    case 2 -> "gm s";
                    default -> "";
                };
            } else {
                // 單人模式或一般伺服器
                cmd = switch(selectedIndex) {
                    case 0 -> "gamemode creative";
                    case 1 -> "gamemode spectator";
                    case 2 -> "gamemode survival";
                    default -> "";
                };
            }

            if (!cmd.isEmpty()) {
                client.player.networkHandler.sendChatCommand(cmd);
            }
        }
    }
}
