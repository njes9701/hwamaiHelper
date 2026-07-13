package org.NJ.hwamaihelper.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import org.NJ.hwamaihelper.client.components.ConfigRow;
import org.NJ.hwamaihelper.client.logic.MalilibInputHandler;
import org.NJ.hwamaihelper.client.utils.KeyRecorder;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class CommandListScreen extends GuiBase {
    private CommandListWidget listWidget;
    private final Minecraft client;

    private ConfigRow activeRow = null;
    private final Set<Integer> pressedKeys = new HashSet<>();

    public CommandListScreen() {
        this.client = Minecraft.getInstance();
        setTitle("自訂指令");
    }

    @Override
    public void initGui() {
        super.initGui();

        // 初始化滾動列表區域
        this.listWidget = new CommandListWidget(client, this.width - 120, this.height - 90, 40, 24);
        this.listWidget.setX(110);

        for (NJConfig.Entry e : NJConfigManager.getInstance().entries) {
            this.listWidget.addEntry(new CommandEntry(e.command, e.key, e.onRelease, e.enabled));
        }

        addButton(new ButtonGeneric((this.width + 100) / 2 - 75, this.height - 35, 150, 20, "§a+ 增加指令"), (button, mouseButton) -> {
            CommandEntry newEntry = new CommandEntry("", "", true, true);
            this.listWidget.addEntry(newEntry);
        });
        addButton(new ButtonGeneric(10, 28, 90, 20, "返回設定"),
                (button, mouseButton) -> closeGui(true));
        addButton(new ButtonGeneric(10, 52, 90, 20, "暱稱編輯器"),
                (button, mouseButton) -> GuiBase.openGui(new NickNameSettingScreen().setParent(this)));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        // 如果這個頁面未來有滾動條，就在這裡轉發給滾動條
        // 目前暫時回傳 false
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        this.listWidget.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        for (CommandEntry entry : listWidget.children()) {
            // 檢查是否點擊了按鍵綁定欄位
            if (entry.row.keyField.isMouseOver(click.x(), click.y())) {
                if (activeRow == entry.row) {
                    // 如果已經是活動行，表示使用者要綁定滑鼠按鍵
                    pressedKeys.clear();
                    pressedKeys.add(click.button());
                    entry.row.keyField.setValue(KeyRecorder.convertToComponent(pressedKeys));
                    activeRow = null; // 結束錄製
                    pressedKeys.clear();
                } else {
                    // 設為活動行，準備開始錄製鍵盤
                    activeRow = entry.row;
                    entry.row.keyField.setValue("> 請按下按鍵 <");
                    pressedKeys.clear();
                }
                listWidget.setFocused(null);
                return true;
            }
        }

        // 點擊其他地方，取消錄製狀態
        if (activeRow != null) {
            // 如果之前有內容，恢復顯示
            String originalKey = "";
            for (NJConfig.Entry e : NJConfigManager.getInstance().entries) {
                if (activeRow.cmdField.getValue().equals(e.command)) {
                    originalKey = e.key;
                    break;
                }
            }
            activeRow.keyField.setValue(originalKey);
            activeRow = null;
        }

        return listWidget.mouseClicked(click, doubled) || super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (activeRow != null) return true;

        // 現在參數類型一致了：都是 CharacterEvent
        return listWidget.charTyped(input) || super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (activeRow != null) {
            int code = input.key();
            if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER || code == GLFW.GLFW_KEY_ESCAPE) {
                activeRow = null;
                pressedKeys.clear();
                return true;
            }
            pressedKeys.add(code);
            activeRow.keyField.setValue(KeyRecorder.convertToComponent(pressedKeys));
            return true;
        }
        return listWidget.keyPressed(input) || super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        pressedKeys.remove(input.key());
        return listWidget.keyReleased(input);
    }

    public void save() {
        NJConfig config = NJConfigManager.getInstance();
        config.entries.clear();
        for (CommandEntry entry : listWidget.children()) {
            String cmd = entry.row.cmdField.getValue().trim();
            if (!cmd.isEmpty()) {
                config.entries.add(new NJConfig.Entry(cmd, entry.row.keyField.getValue(), entry.row.onRelease, entry.row.enabled));
            }
        }
        NJConfigManager.save();
        MalilibInputHandler.getInstance().refreshCustomCommandHotkeys();
    }

    @Override
    public void removed() {
        if (this.listWidget != null) {
            save();
        }
        super.removed();
    }

    private class CommandListWidget extends ContainerObjectSelectionList<CommandEntry> {
        public CommandListWidget(Minecraft client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
        }
        public void remove(CommandEntry entry) { this.removeEntry(entry); }
        @Override public int getRowWidth() { return 280; }
        @Override protected int scrollBarX() { return this.getX() + this.getWidth() - 8; }
        @Override public int addEntry(CommandEntry entry) { return super.addEntry(entry); }
    }

    private class CommandEntry extends ContainerObjectSelectionList.Entry<CommandEntry> {
        public final ConfigRow row;
        public CommandEntry(String cmd, String key, boolean onRelease, boolean enabled) {
            this.row = new ConfigRow(client.font, cmd, key, onRelease, enabled, () -> listWidget.remove(this));
        }
        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            row.updatePosition(this.getX() + this.getWidth() / 2, this.getY());
            row.cmdField.extractRenderState(context, mouseX, mouseY, deltaTicks);
            row.keyField.extractRenderState(context, mouseX, mouseY, deltaTicks);
            row.triggerModeBtn.extractRenderState(context, mouseX, mouseY, deltaTicks);
            row.toggleBtn.extractRenderState(context, mouseX, mouseY, deltaTicks);
            row.deleteBtn.extractRenderState(context, mouseX, mouseY, deltaTicks);
            if (CommandListScreen.this.activeRow == this.row) {
                context.fill(row.keyField.getX() - 2, row.keyField.getY() - 2,
                        row.keyField.getX() + row.keyField.getWidth() + 2,
                        row.keyField.getY() + row.keyField.getHeight() + 2, 0x88FFFF00);
            }
        }
        @Override public List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() { return List.of(row.cmdField, row.keyField, row.triggerModeBtn, row.toggleBtn, row.deleteBtn); }
        @Override public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() { return List.of(row.cmdField, row.keyField, row.triggerModeBtn, row.toggleBtn, row.deleteBtn); }
    }
}
