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

public class GetItemScreen extends Screen {
    private final List<ItemButton> buttons = new ArrayList<>();
    private int guiLeft, guiTop;
    private final int windowWidth = 80;
    private final int windowHeight = 80;

    public GetItemScreen() {
        super(Text.of("取得物品"));
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - windowWidth) / 2;
        this.guiTop = (this.height - windowHeight) / 2;
        this.buttons.clear();

        // Row 1
        addButton(0, 0, Items.FIREWORK_ROCKET, "煙火");
        addButton(1, 0, Items.ITEM_FRAME, "透明展示框");
        addButton(2, 0, Items.ARROW, "箭矢");

        // Row 2
        addButton(0, 1, Items.PLAYER_HEAD, "頭顱");
        addButton(1, 1, Items.MINECART, "車");
        addButton(2, 1, Items.ELYTRA, "鞘翅");

        // Row 3
        addButton(0, 2, Items.PHANTOM_MEMBRANE, "夜魅皮膜");
        addButton(1, 2, Items.LIGHT, "光源");
    }

    private void addButton(int col, int row, Item item, String cmdName) {
        int x = guiLeft + 10 + (col * 20);
        int y = guiTop + 10 + (row * 20);
        buttons.add(new ItemButton(x, y, item, cmdName));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Use fill instead of renderBackground to prevent blur shader crash
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Window
        context.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + windowHeight, 0xCC000000);
        
        // Border
        int color = 0xFFFFFFFF;
        context.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + 1, color);
        context.fill(guiLeft, guiTop + windowHeight - 1, guiLeft + windowWidth, guiTop + windowHeight, color);
        context.fill(guiLeft, guiTop, guiLeft + 1, guiTop + windowHeight, color);
        context.fill(guiLeft + windowWidth - 1, guiTop, guiLeft + windowWidth, guiTop + windowHeight, color);

        for (ItemButton btn : buttons) {
            btn.render(context, mouseX, mouseY);
        }
        
        for (ItemButton btn : buttons) {
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
        
        for (ItemButton btn : buttons) {
            if (btn.isHovered(mouseX, mouseY)) {
                if (client != null && client.player != null) {
                    client.player.networkHandler.sendChatCommand("chmc 取得物品 " + btn.name);
                    this.close();
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }
    
    @Override
    public boolean shouldPause() { return false; }

    private static class ItemButton {
        int x, y;
        Item item;
        String name;

        public ItemButton(int x, int y, Item item, String name) {
            this.x = x; this.y = y; this.item = item; this.name = name;
        }

        public void render(DrawContext context, int mx, int my) {
            if (isHovered(mx, my)) {
                context.fill(x, y, x + 18, y + 18, 0x55FFFFFF);
            }
            context.drawItem(new ItemStack(item), x + 1, y + 1);
        }

        public boolean isHovered(double mx, double my) {
            return mx >= x && mx < x + 18 && my >= y && my < y + 18;
        }
    }
}
