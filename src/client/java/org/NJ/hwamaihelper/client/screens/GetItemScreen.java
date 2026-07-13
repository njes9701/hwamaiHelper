package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GetItemScreen extends GuiBase {
    private final List<ItemButton> buttons = new ArrayList<>();
    private int guiLeft, guiTop;
    private final int windowWidth = 80;
    private final int windowHeight = 80;

    public GetItemScreen() {
        setTitle("取得物品");
    }

    @Override
    public void initGui() {
        super.initGui();
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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
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
            btn.extractRenderState(context, mouseX, mouseY);
        }
        
        for (ItemButton btn : buttons) {
             if (btn.isHovered(mouseX, mouseY)) {
                 context.setTooltipForNextFrame(font, Component.literal(btn.name), mouseX, mouseY);
             }
        }
        
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        
        for (ItemButton btn : buttons) {
            if (btn.isHovered(mouseX, mouseY)) {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.connection.sendCommand("chmc 取得物品 " + btn.name);
                    this.onClose();
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }
    
    @Override
    public boolean isPauseScreen() { return false; }

    private static class ItemButton {
        int x, y;
        Item item;
        String name;

        public ItemButton(int x, int y, Item item, String name) {
            this.x = x; this.y = y; this.item = item; this.name = name;
        }

        public void extractRenderState(GuiGraphicsExtractor context, int mx, int my) {
            if (isHovered(mx, my)) {
                context.fill(x, y, x + 18, y + 18, 0x55FFFFFF);
            }
            context.item(new ItemStack(item), x + 1, y + 1);
        }

        public boolean isHovered(double mx, double my) {
            return mx >= x && mx < x + 18 && my >= y && my < y + 18;
        }
    }
}
