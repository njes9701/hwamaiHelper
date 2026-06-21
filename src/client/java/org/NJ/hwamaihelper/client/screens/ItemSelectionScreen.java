package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import org.NJ.hwamaihelper.client.utils.EnglishTranslationHelper;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemSelectionScreen extends Screen {
    private final Screen parent;
    private final Consumer<String> onSelect;
    private EditBox searchBox;
    private List<Item> allItems;
    private List<Item> filteredItems;
    private int scrollOffset = 0;
    private int itemsPerRow = 9;
    private int rowsPerPage = 5;
    private final int slotSize = 20;

    public ItemSelectionScreen(Screen parent, Consumer<String> onSelect) {
        super(Component.literal("選擇物品"));
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

        this.searchBox = new EditBox(font, width / 2 - 100, 15, 200, 20, Component.literal("搜尋物品..."));
        this.searchBox.setResponder(this::filterItems);
        this.addWidget(this.searchBox);
        this.setInitialFocus(this.searchBox);

        if (allItems == null) {
            allItems = BuiltInRegistries.ITEM.stream().collect(Collectors.toList());
            filteredItems = new ArrayList<>(allItems);
        }
    }

    private void filterItems(String query) {
        String normalizedQuery = normalizeSearchText(query);
        boolean englishSearchEnabled = NJConfigManager.getInstance().enableEnglishSearch;

        filteredItems = allItems.stream()
                .filter(item -> {
                    String id = BuiltInRegistries.ITEM.getKey(item).toString();
                    String idPath = BuiltInRegistries.ITEM.getKey(item).getPath();
                    String name = Component.translatable(item.getDescriptionId()).getString();
                    String enName = englishSearchEnabled ? EnglishTranslationHelper.translate(item.getDescriptionId()) : "";

                    return containsSearchText(normalizedQuery, id, idPath, name, enName);
                })
                .collect(Collectors.toList());
        scrollOffset = 0;
    }

    private static boolean containsSearchText(String query, String... values) {
        if (query.isEmpty()) {
            return true;
        }

        for (String value : values) {
            if (normalizeSearchText(value).contains(query)) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeSearchText(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // 渲染全螢幕背景暗化
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        int gridWidth = itemsPerRow * slotSize;
        int gridHeight = rowsPerPage * slotSize;
        int startX = (width - gridWidth) / 2;
        int startY = 45;

        // 渲染容器背景
        context.fill(startX - 5, startY - 5, startX + gridWidth + 5, startY + gridHeight + 5, 0xCC000000);
        
        searchBox.extractRenderState(context, mouseX, mouseY, delta);

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
            
            context.item(new ItemStack(item), x + 2, y + 2);
        }

        // 渲染 Tooltip (放在最後確保不被遮擋)
        for (int i = 0; i < itemsPerRow * rowsPerPage; i++) {
            int index = i + scrollOffset * itemsPerRow;
            if (index >= filteredItems.size()) break;
            int x = startX + (i % itemsPerRow) * slotSize;
            int y = startY + (i / itemsPerRow) * slotSize;
            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                Item item = filteredItems.get(index);
                context.setTooltipForNextFrame(font, Component.translatable(item.getDescriptionId()), mouseX, mouseY);
            }
        }

        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
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
                onSelect.accept(BuiltInRegistries.ITEM.getKey(item).toString());
                this.minecraft.setScreenAndShow(parent);
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
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == 256) { // ESC
            this.minecraft.setScreenAndShow(parent);
            return true;
        }
        return searchBox.keyPressed(input) || super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        return searchBox.charTyped(input);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(parent);
    }
}
