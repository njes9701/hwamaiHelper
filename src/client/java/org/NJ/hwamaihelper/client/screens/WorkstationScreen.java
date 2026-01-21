package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class WorkstationScreen extends Screen {
    private final List<WorkstationButton> buttons = new ArrayList<>();
    private int guiLeft, guiTop;
    private final int windowWidth = 80; // 18*3 + padding
    private final int windowHeight = 80;

    public WorkstationScreen() {
        super(Text.of("工作方塊"));
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - windowWidth) / 2;
        this.guiTop = (this.height - windowHeight) / 2;
        this.buttons.clear();

        // Row 1
        addButton(0, 0, Items.CRAFTING_TABLE, "工作台");
        addButton(1, 0, Items.STONECUTTER, "切石機");
        addButton(2, 0, Items.CARTOGRAPHY_TABLE, "製圖桌");

        // Row 2
        addButton(0, 1, Items.LOOM, "紡織機");
        addButton(1, 1, Items.SMITHING_TABLE, "鍛造台");
        addButton(2, 1, Items.GRINDSTONE, "砂輪");

        // Row 3
        addButton(0, 2, Items.LAVA_BUCKET, "垃圾桶");
        addButton(1, 2, Items.ENDER_CHEST, "終界箱");
        addButton(2, 2, Items.ANVIL, "鐵砧");
    }

    private void addButton(int col, int row, Item item, String cmdName) {
        int x = guiLeft + 10 + (col * 20);
        int y = guiTop + 10 + (row * 20);
        buttons.add(new WorkstationButton(x, y, item, cmdName));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Draw Window Background
        context.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + windowHeight, 0xCC000000); // Semi-transparent black
        // Manual border
        int color = 0xFFFFFFFF;
        context.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + 1, color); // Top
        context.fill(guiLeft, guiTop + windowHeight - 1, guiLeft + windowWidth, guiTop + windowHeight, color); // Bottom
        context.fill(guiLeft, guiTop, guiLeft + 1, guiTop + windowHeight, color); // Left
        context.fill(guiLeft + windowWidth - 1, guiTop, guiLeft + windowWidth, guiTop + windowHeight, color); // Right

        for (WorkstationButton btn : buttons) {
            btn.render(context, mouseX, mouseY);
        }
        
        // Render tooltip if hovered
        for (WorkstationButton btn : buttons) {
             if (btn.isHovered(mouseX, mouseY)) {
                 context.drawTooltip(textRenderer, Text.of(btn.name), mouseX, mouseY);
             }
        }
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        
        for (WorkstationButton btn : buttons) {
            if (btn.isHovered(mouseX, mouseY)) {
                if (client != null && client.player != null) {
                    client.player.networkHandler.sendChatCommand("chmc 開啟介面 " + btn.name);
                    this.close();
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }
    
    @Override
    public boolean shouldPause() { return false; }

    private static class WorkstationButton {
        int x, y;
        Item item;
        String name;

        public WorkstationButton(int x, int y, Item item, String name) {
            this.x = x; this.y = y; this.item = item; this.name = name;
        }

        public void render(DrawContext context, int mx, int my) {
            boolean hovered = isHovered(mx, my);
            if (hovered) {
                context.fill(x, y, x + 18, y + 18, 0x55FFFFFF);
            }
            context.drawItem(new ItemStack(item), x + 1, y + 1);
        }

        public boolean isHovered(double mx, double my) {
            return mx >= x && mx < x + 18 && my >= y && my < y + 18;
        }
    }
}