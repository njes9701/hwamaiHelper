package org.NJ.hwamaihelper.client.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

public class ChunkDistanceComponent {
    private final DistanceSlider slider;
    private final ButtonWidget resetBtn;
    private final NJConfig config = NJConfigManager.getInstance();

    public ChunkDistanceComponent(int x, int y, int totalWidth, Runnable onReset) {
        // 1. 計算分配寬度
        int spacing = 5;
        int buttonWidth = 30; // 按鈕寬度縮小，僅顯示"重置"
        int sliderWidth = totalWidth - buttonWidth - spacing; // 剩餘寬度給滑動條

        double initialVal = (double) (config.chunkLoadDistance - 5) / 27;

        // 2. 初始化滑動條 (位置在 x)
        this.slider = new DistanceSlider(x, y, sliderWidth, 20, initialVal);

        // 3. 初始化重置按鈕 (位置在 x + 滑動條寬 + 間距，且 Y 座標與滑動條相同)
        this.resetBtn = ButtonWidget.builder(Text.of("重置"), b -> {
            config.chunkLoadDistance = 10;
            onReset.run();
        }).dimensions(x + sliderWidth + spacing, y, buttonWidth, 20).build();
    }

    public void save(MinecraftClient client) {
        if (config.chunkLoadDistance != config.lastChunkDistance) {
            if (client.player != null) {
                client.player.networkHandler.sendChatCommand("chmc 設定 自己 強載入距離 " + config.chunkLoadDistance);
            }
            config.lastChunkDistance = config.chunkLoadDistance;
        }
    }

    public DistanceSlider getSlider() { return slider; }
    public ButtonWidget getResetBtn() { return resetBtn; }

    public static class DistanceSlider extends SliderWidget {
        public DistanceSlider(int x, int y, int w, int h, double v) {
            super(x, y, w, h, Text.of("強載入距離: " + (5 + (int)(v * 27))), v);
        }
        @Override protected void updateMessage() { this.setMessage(Text.of("強載入距離: " + (5 + (int)(this.value * 27)))); }
        @Override protected void applyValue() { NJConfigManager.getInstance().chunkLoadDistance = 5 + (int)(this.value * 27); }
        public void setProgressManually(double mx) {
            this.value = Math.max(0.0, Math.min(1.0, (mx - (double)this.getX()) / (double)this.getWidth()));
            this.applyValue(); this.updateMessage();
        }
    }
}