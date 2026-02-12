package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.utils.EnglishTranslationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemSelectionScreen extends Screen {
    private final Screen parent;
    private final Consumer<String> onSelect;
    private TextFieldWidget searchBox;
    private List<Item> allItems;
    private List<Item> filteredItems;
    private int scrollOffset = 0;
    private int itemsPerRow = 9;
    private int rowsPerPage = 5;
    private final int slotSize = 20;

    public ItemSelectionScreen(Screen parent, Consumer<String> onSelect) {
        super(Text.of("選擇物品"));
        this.parent = parent;
        this.onSelect = onSelect;
    }

    @Override
    protected void init() {
        // 動態計算佈局
        int availableWidth = this.width - 40;
        int availableHeight = this.height - 80; // 扣除頂部搜尋框與邊距
        
        this.itemsPerRow = Math.max(5, availableWidth / slotSize);
        this.rowsPerPage = Math.max(3, availableHeight / slotSize);

        this.searchBox = new TextFieldWidget(textRenderer, width / 2 - 100, 15, 200, 20, Text.of("搜尋物品..."));
        this.searchBox.setChangedListener(this::filterItems);
        this.addSelectableChild(this.searchBox);
        this.setInitialFocus(this.searchBox);

        if (allItems == null) {
            allItems = Registries.ITEM.stream().collect(Collectors.toList());
            filteredItems = new ArrayList<>(allItems);
        }
    }

    private void filterItems(String query) {
        String lowerQuery = query.toLowerCase();
        filteredItems = allItems.stream()
                .filter(item -> {
                    String id = Registries.ITEM.getId(item).toString().toLowerCase();
                    String name = item.getName().getString().toLowerCase();
                    String enName = EnglishTranslationHelper.translate(item.getTranslationKey()).toLowerCase();
                    return id.contains(lowerQuery) || name.contains(lowerQuery) || enName.contains(lowerQuery);
                })
                .collect(Collectors.toList());
        scrollOffset = 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染全螢幕背景暗化
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        int gridWidth = itemsPerRow * slotSize;
        int gridHeight = rowsPerPage * slotSize;
        int startX = (width - gridWidth) / 2;
        int startY = 45;

        // 渲染容器背景
        context.fill(startX - 5, startY - 5, startX + gridWidth + 5, startY + gridHeight + 5, 0xCC000000);
        
        searchBox.render(context, mouseX, mouseY, delta);

        for (int i = 0; i < itemsPerRow * rowsPerPage; i++) {
            int index = i + scrollOffset * itemsPerRow;
            if (index >= filteredItems.size()) break;

            int x = startX + (i % itemsPerRow) * slotSize;
            int y = startY + (i / itemsPerRow) * slotSize;

            Item item = filteredItems.get(index);
            boolean hovered = mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize;
            
            if (hovered) {
                context.fill(x, y, x + slotSize, y + slotSize, 0x55FFFFFF);
            }
            
            context.drawItem(new ItemStack(item), x + 2, y + 2);
        }

        // 渲染 Tooltip (放在最後確保不被遮擋)
        for (int i = 0; i < itemsPerRow * rowsPerPage; i++) {
            int index = i + scrollOffset * itemsPerRow;
            if (index >= filteredItems.size()) break;
            int x = startX + (i % itemsPerRow) * slotSize;
            int y = startY + (i / itemsPerRow) * slotSize;
            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                Item item = filteredItems.get(index);
                context.drawTooltip(textRenderer, item.getName(), mouseX, mouseY);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (searchBox.mouseClicked(click, doubled)) return true;

        int gridWidth = itemsPerRow * slotSize;
        int startX = (width - gridWidth) / 2;
        int startY = 45;
        double mouseX = click.x();
        double mouseY = click.y();

        for (int i = 0; i < itemsPerRow * rowsPerPage; i++) {
            int index = i + scrollOffset * itemsPerRow;
            if (index >= filteredItems.size()) break;

            int x = startX + (i % itemsPerRow) * slotSize;
            int y = startY + (i / itemsPerRow) * slotSize;

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                Item item = filteredItems.get(index);
                onSelect.accept(Registries.ITEM.getId(item).toString());
                client.setScreen(parent);
                return true;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else if (verticalAmount < 0) {
            int maxScroll = Math.max(0, (filteredItems.size() + itemsPerRow - 1) / itemsPerRow - rowsPerPage);
            scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256) { // ESC
            client.setScreen(parent);
            return true;
        }
        return searchBox.keyPressed(input) || super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return searchBox.charTyped(input);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
