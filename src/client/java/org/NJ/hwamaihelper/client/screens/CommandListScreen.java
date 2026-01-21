package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.client.components.ConfigRow;
import org.NJ.hwamaihelper.client.utils.KeyRecorder;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class CommandListScreen implements NJTab {
    private CommandListWidget listWidget;
    private ButtonWidget addButton;
    private final MinecraftClient client;
    private int width, height;

    private ConfigRow activeRow = null;
    private final Set<Integer> pressedKeys = new HashSet<>();

    public CommandListScreen() {
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;

        // 初始化滾動列表區域
        this.listWidget = new CommandListWidget(client, width - 120, height - 90, 40, 24);
        this.listWidget.setX(110);

        for (NJConfig.Entry e : NJConfigManager.getInstance().entries) {
            this.listWidget.addEntry(new CommandEntry(e.command, e.key));
        }

        this.addButton = ButtonWidget.builder(Text.of("§a+ 增加指令"), b -> {
            CommandEntry newEntry = new CommandEntry("", "");
            this.listWidget.addEntry(newEntry);
            this.listWidget.setScrollY(this.listWidget.getMaxScrollY());
        }).dimensions((width + 100) / 2 - 75, height - 35, 150, 20).build();
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        // 如果這個頁面未來有滾動條，就在這裡轉發給滾動條
        // 目前暫時回傳 false
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.listWidget.render(context, mouseX, mouseY, delta);
        this.addButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (addButton.mouseClicked(click, doubled)) return true;

        for (CommandEntry entry : listWidget.children()) {
            if (entry.row.keyField.isMouseOver(click.x(), click.y())) {
                activeRow = entry.row;
                entry.row.keyField.setText("> 請按下按鍵 <");
                listWidget.setFocused(null);
                return true;
            }
        }

        activeRow = null;
        return listWidget.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (activeRow != null) return true;

        // 現在參數類型一致了：都是 CharInput
        return listWidget.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (activeRow != null) {
            int code = input.key();
            if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER || code == GLFW.GLFW_KEY_ESCAPE) {
                activeRow = null;
                pressedKeys.clear();
                return true;
            }
            pressedKeys.add(code);
            activeRow.keyField.setText(KeyRecorder.convertToText(pressedKeys));
            return true;
        }
        return listWidget.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        pressedKeys.remove(input.key());
        return listWidget.keyReleased(input);
    }

    @Override
    public void save() {
        NJConfig config = NJConfigManager.getInstance();
        config.entries.clear();
        for (CommandEntry entry : listWidget.children()) {
            String cmd = entry.row.cmdField.getText().trim();
            if (!cmd.isEmpty()) {
                config.entries.add(new NJConfig.Entry(cmd, entry.row.keyField.getText()));
            }
        }
        NJConfigManager.save();
    }

    private class CommandListWidget extends ElementListWidget<CommandEntry> {
        public CommandListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
        }
        public void remove(CommandEntry entry) { this.removeEntry(entry); }
        @Override public int getRowWidth() { return 280; }
        @Override protected int getScrollbarX() { return this.getX() + this.width - 8; }
        @Override public int addEntry(CommandEntry entry) { return super.addEntry(entry); }
    }

    private class CommandEntry extends ElementListWidget.Entry<CommandEntry> {
        public final ConfigRow row;
        public CommandEntry(String cmd, String key) {
            this.row = new ConfigRow(client.textRenderer, cmd, key, () -> listWidget.remove(this));
        }
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            row.updatePosition(this.getX() + this.getWidth() / 2, this.getY());
            row.cmdField.render(context, mouseX, mouseY, deltaTicks);
            row.keyField.render(context, mouseX, mouseY, deltaTicks);
            row.deleteBtn.render(context, mouseX, mouseY, deltaTicks);
            if (CommandListScreen.this.activeRow == this.row) {
                context.fill(row.keyField.getX() - 2, row.keyField.getY() - 2,
                        row.keyField.getX() + row.keyField.getWidth() + 2,
                        row.keyField.getY() + row.keyField.getHeight() + 2, 0x88FFFF00);
            }
        }
        @Override public List<? extends net.minecraft.client.gui.Element> children() { return List.of(row.cmdField, row.deleteBtn); }
        @Override public List<? extends net.minecraft.client.gui.Selectable> selectableChildren() { return List.of(row.cmdField, row.deleteBtn); }
    }
}