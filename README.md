# 華麥助手 (HwamaiHelper)

HwamaiHelper 是一個 Fabric 用戶端輔助模組，主要為華麥伺服器設計，提供快捷指令、快速介面、創造欄英文搜尋、暱稱編輯、自動補煙火與快速整地等功能。

HwamaiHelper is a Fabric client-side assistant mod designed for the Hwamai server. It provides command shortcuts, quick menus, Creative inventory English search, nickname editing, firework replenishment, and quick land leveling.

## 版本資訊 (Version)

* Mod Version: `1.4.1`
* Minecraft: `26.2`
* Fabric Loader: `0.19.3+`
* Fabric API: `0.154.0+26.2`
* MaLiLib: `0.29.2+`（必要依賴 / Required）
* Java: `25`

> **必要依賴：** 本模組目前使用 MaLiLib 提供設定介面與快捷鍵功能。安裝 HwamaiHelper 前，必須先安裝 Minecraft `26.2` 對應的 MaLiLib `0.29.2` 或更新版本，否則 Fabric Loader 將無法啟動本模組。
>
> **Required dependency:** HwamaiHelper uses MaLiLib for its configuration GUI and keybind system. Install MaLiLib `0.29.2` or newer for Minecraft `26.2` before launching the mod.

## 主要功能 (Features)

### 快捷指令選單 (Quick Command Menu)

* 預設快捷鍵：`X + F`，可在個人設定中修改。
* Default keybinding: `X + F`, configurable in Personal Settings.
* 提供圖形化主介面，集中管理指令列表、個人設定與暱稱設定。
* Provides a GUI for command lists, personal settings, and nickname settings.

### 自定義指令 (Custom Commands)

* 可在「指令列表」新增、修改、刪除自訂指令。
* Add, edit, and remove custom shortcut commands in the Command List tab.
* 支援單鍵與組合鍵，例如 `G`、`Ctrl + Y`、`Ctrl + Shift + Z`。
* Supports single keys and key combinations such as `G`, `Ctrl + Y`, and `Ctrl + Shift + Z`.
* 每個指令可設定啟用/停用，以及「按下觸發」或「放開觸發」。
* Each command can be enabled/disabled and configured to trigger on press or release.
* 指令會自動移除開頭 `/` 後送出，適合快速執行伺服器指令。
* Commands are sent without a leading `/`, making them suitable for server commands.

### 個人設定 (Personal Settings)

* 可設定主選單、工作方塊介面、取得物品介面、遊戲模式輪盤、快速整地等快捷鍵。
* Configure keybindings for the main menu, workstation menu, get-item menu, game mode wheel, and quick land leveling.
* 支援個別啟用/停用功能，以及按下/放開觸發模式。
* Supports per-feature enable toggles and press/release trigger modes.
* 可設定遊戲模式輪盤排除手持物，拿著指定物品時不觸發輪盤。
* The game mode wheel can ignore activation while holding a configured excluded item.
* 排除物以 Minecraft 物品 ID 設定，例如 `minecraft:stick`。
* Configure the excluded item with its Minecraft item ID, such as `minecraft:stick`.

### 創造模式英文搜尋 (Creative Mode English Search)

* 在非英文語言環境下，也能直接於原版創造模式物品欄搜尋英文物品名稱。
* Search English item names directly in the vanilla Creative inventory even when the game language is not English.
* 範例：`redstone`、`diamond sword`、`crafting table`。
* Examples: `redstone`, `diamond sword`, `crafting table`.
* 保留原版 ID 搜尋，例如 `minecraft:redstone`。
* Vanilla ID search such as `minecraft:redstone` remains supported.
* 可在個人設定中開啟或關閉。
* Toggleable in Personal Settings.

### 遊戲模式輪盤 (Game Mode Wheel)

* 預設快捷鍵：`Alt`，可自訂。
* Default keybinding: `Alt`, configurable.
* 按住快捷鍵顯示輪盤，移動滑鼠選擇模式，放開後執行切換。
* Hold the key to show the wheel, move the mouse to select a mode, and release to execute.
* 支援 Creative、Spectator、Survival。
* Supports Creative, Spectator, and Survival.
* 在華麥伺服器使用 `gm c`、`gm sp`、`gm s`；其他伺服器使用 vanilla `gamemode` 指令。
* Uses `gm c`, `gm sp`, and `gm s` on Hwamai; uses vanilla `gamemode` commands elsewhere.
* 可設定排除手持物，避免與特定物品操作衝突。
* Supports an excluded held item to avoid conflicts with item actions.

### 工作方塊介面 (Workstation Menu)

* 預設快捷鍵：`Shift + G`，可自訂。
* Default keybinding: `Shift + G`, configurable.
* 點擊圖示快速送出華麥伺服器的工作方塊介面指令。
* Click an icon to send the Hwamai workstation interface command.
* 目前支援：工作台、切石機、製圖台、織布機、鍛造台、砂輪、垃圾桶、終界箱、鐵砧。
* Supported entries: Crafting Table, Stonecutter, Cartography Table, Loom, Smithing Table, Grindstone, Trash Can, Ender Chest, and Anvil.

### 取得物品介面 (Get Item Menu)

* 預設快捷鍵：`G`，可自訂。
* Default keybinding: `G`, configurable.
* 點擊圖示快速送出華麥伺服器的取得物品指令。
* Click an icon to send the Hwamai get-item command.
* 目前支援：煙火、隱形物品展示框、箭矢、玩家頭顱、礦車、鞘翅、幻翼膜、光源方塊。
* Supported entries: Firework Rockets, Invisible Item Frames, Arrows, Player Heads, Minecarts, Elytra, Phantom Membranes, and Light Blocks.

### 自動補充煙火 (Auto Firework Replenishment)

* 可在個人設定中開啟或關閉。
* Toggleable in Personal Settings.
* 當煙火數量低於 5，且剛使用煙火造成數量下降時，自動送出 `chmc 取得物品 煙火`。
* When firework count drops below 5 after use, it sends `chmc 取得物品 煙火`.
* 有 5 秒冷卻，避免重複送出指令。
* Has a 5-second cooldown to avoid command spam.
* 只在非 GUI 操作且非丟棄物品情境下觸發。
* Triggers only outside GUI/drop scenarios.

### 快速整地 (Quick Land Leveling)

* 可在個人設定中指定切換快捷鍵；未設定快捷鍵時不觸發。
* Toggle keybinding can be configured in Personal Settings; it remains inactive until a key is assigned.
* 開啟後按住原版攻擊/破壞鍵，會自動選取視線附近、可秒挖的方塊進行破壞。
* While enabled, hold the normal attack/break key to automatically target nearby instant-break blocks.
* 僅處理玩家目前 Y 座標以上、互動距離內、且視線可達的方塊，降低誤挖風險。
* Only targets blocks at or above the player's current Y level, within interaction range, and passing line-of-sight checks.
* 每 tick 目標數可設定為 `1` 到 `10`，用來控制速度與穩定性。
* Targets per tick can be configured from `1` to `10` to balance speed and stability.

### 暱稱編輯器 (Nickname Editor)

* 支援多段文字編輯，每段可設定文字、顏色與效果。
* Supports multi-section nickname editing with independent text, color, and effects.
* 支援即時預覽，包含陰影效果預覽。
* Supports real-time preview, including shadow preview.
* 基本樣式：粗體、斜體、底線、刪除線、亂碼。
* Basic styles: Bold, Italic, Underline, Strikethrough, and Obfuscated.
* 特殊效果：彩虹、漸層、自訂陰影。
* Special effects: Rainbow, Gradient, and Custom Shadow.
* 提供 HSV 調色盤，用於直覺選色。
* Includes an HSV color picker for visual color selection.
* 可將暱稱存入存檔欄位，日後可預覽、套用或刪除。
* Save slots allow previewing, applying, or deleting saved nicknames.
* 套用暱稱會送出 `chmc 設定 自己 暱稱 ...`；取消暱稱會送出 `chmc 設定 自己 取消暱稱`。
* Applying sends `chmc 設定 自己 暱稱 ...`; cancelling sends `chmc 設定 自己 取消暱稱`.

## 安裝與使用 (Installation)

1. 安裝 Minecraft `26.2` 對應的 Fabric Loader。
2. 安裝 Fabric API `0.154.0+26.2`。
3. 安裝 MaLiLib `0.29.2+`；此為必要依賴，不能省略。
4. 將 `hwamaiHelper-1.4.1.jar`、Fabric API 與 MaLiLib 放入 `.minecraft/mods`。
5. 啟動遊戲並加入伺服器。

## 設定檔 (Configuration)

設定檔位於 `.minecraft/config/nj_config.json`。

Main configuration fields include:

* `enabled`: 是否啟用模組主功能。
* `autoReplenishFireworks`: 是否啟用自動補煙火。
* `enableEnglishSearch`: 是否啟用創造欄英文搜尋。
* `openMenuKey`: 主選單快捷鍵。
* `openWorkstationKey`: 工作方塊介面快捷鍵。
* `openGetItemKey`: 取得物品介面快捷鍵。
* `gameModeWheelKey`: 遊戲模式輪盤快捷鍵。
* `gameModeWheelExcludeItem`: 輪盤排除手持物 ID。
* `quickLandLevelingKey`: 快速整地切換快捷鍵。
* `quickLandLevelingTargetsPerTick`: 快速整地每 tick 目標數。
* `entries`: 自訂快捷指令列表。

## 開發與編譯 (Building)

請使用 Java `25`。

```bash
./gradlew build
```

編譯完成後，輸出檔會在 `build/libs`。

## 注意事項 (Notes)

* 本模組是用戶端輔助工具，部分功能依賴華麥伺服器指令。
* This is a client-side assistant mod, and some features depend on Hwamai server commands.
* 使用自動化與快捷功能時，請遵守伺服器規則。
* Follow server rules when using automation or shortcut features.
