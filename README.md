# 華麥助手 (HwamaiHelper)
# HwamaiHelper

這是一個專為華麥伺服器設計的 Fabric 客戶端輔助模組，提供便捷的快捷鍵、指令選單、暱稱編輯器以及各種自動化功能，旨在提升遊玩體驗。
A Fabric client-side assistant mod designed specifically for the Hwamai server, providing convenient keybindings, command menus, a nickname editor, and various automation features to enhance the gameplay experience.

## ✨ 主要功能
## ✨ Main Features

### 1. 快捷指令選單
### 1. Quick Command Menu
*   **預設快捷鍵**：`X + F` (可自訂)
*   **Default Keybinding**: `X + F` (Customizable)
*   提供圖形化介面，快速存取常用指令。
*   Provides a graphical interface for quick access to common commands.
*   包含多個功能分頁：指令集、個人設定、暱稱設定。
*   Includes multiple functional tabs: Command Set, Personal Settings, and Nickname Settings.

### 2. 個人設定 (Personal Settings)
### 2. Personal Settings
*   **快捷鍵設定**：
*   **Keybinding Settings:**
    *   **主選單開關**：設定開啟華麥助手主介面的按鍵 (預設 `X + F`)。
    *   **Main Menu Toggle**: Set the key to open the HwamaiHelper main interface (Default: `X + F`).
    *   **工作方塊介面**：設定開啟工作方塊快速選單的按鍵 (預設 `Shift + G`)。
    *   **Workstation Interface**: Set the key to open the Workstation quick menu (Default: `Shift + G`).
*   **材質包管理**：
*   **Resource Pack Management:**
    *   一鍵切換材質包狀態 (自動/維持)。
    *   One-click toggle for resource pack status (Automatic/Maintain).
    *   **自動取消材質包**：可設定進入伺服器時自動輸入指令取消材質包。
    *   **Auto-Cancel Resource Pack**: Can be configured to automatically execute commands to cancel resource packs upon joining the server.
*   **煙火自動補充**：
*   **Auto Firework Replenishment:**
    *   **開關**：開啟後，當背包內的煙火數量不足時自動補充。
    *   **Toggle**: When enabled, automatically replenishes fireworks when the inventory count is low.
    *   **觸發條件**：
    *   **Trigger Conditions:**
        1.  煙火數量小於 5 個。
        1.  Firework count is less than 5.
        2.  剛好**使用**掉煙火導致數量減少 (右鍵使用)。
        2.  Just **used** a firework leading to a decrease in count (Right-click use).
        3.  不在介面中、非丟棄操作。
        3.  Not in a GUI, and not a drop operation.
    *   **冷卻時間**：5 秒。
    *   **Cooldown**: 5 seconds.

### 3. 暱稱編輯器 (Nickname Editor)
### 3. Nickname Editor
*   **圖形化編輯**：
*   **Graphical Editing:**
    *   支援多段文字編輯，每段可獨立設定顏色與樣式。
    *   Supports multi-segment text editing, with independent color and style settings for each segment.
    *   **即時預覽**：上方即時顯示在遊戲中的暱稱效果 (包含自定義陰影)。
    *   **Real-time Preview**: Displays the in-game nickname effect in real-time at the top (including custom shadows).
*   **豐富樣式**：
*   **Rich Styles:**
    *   **基礎樣式**：粗體 (B)、斜體 (I)、底線 (U)、刪除線 (S)、混淆 (O)。
    *   **Basic Styles**: Bold (B), Italic (I), Underline (U), Strikethrough (S), Obfuscated (O).
    *   **特殊效果**：
    *   **Special Effects:**
        *   **彩虹 (R)**：整段文字呈現彩虹流光效果。
        *   **Rainbow (R)**: The entire text segment displays a rainbow flow effect.
        *   **漸層 (G)**：支援雙色漸層過渡。
        *   **Gradient (G)**: Supports two-color gradient transitions.
        *   **陰影 (SH)**：支援自定義陰影顏色 (模組內提供獨家預覽技術)。
        *   **Shadow (SH)**: Supports custom shadow colors (exclusive preview technology provided within the mod).
*   **調色盤**：點擊色塊即可開啟 HSV 調色盤，直覺選擇顏色。
*   **Color Picker**: Click on a color block to open the HSV color picker for intuitive color selection.
*   **存檔功能**：可將設計好的暱稱存入存檔欄位，方便日後切換。
*   **Save Feature**: Save designed nicknames to slots for easy switching later.

### 4. 快速介面 (Quick Interfaces)
### 4. Quick Interfaces
*   **遊戲模式切換輪盤 (Game Mode Wheel)**：
*   **Game Mode Wheel Switcher:**
    *   **快捷鍵**：`Alt` (可自訂)。
    *   **Keybinding**: `Alt` (Customizable).
    *   在遊戲中按住快捷鍵即可開啟輪盤，滑動滑鼠快速切換：**創造**、**觀察者**、**生存**模式。
    *   Hold the keybinding in-game to open the wheel and move the mouse to quickly switch between **Creative**, **Spectator**, and **Survival** modes.
    *   針對華麥伺服器最佳化指令使用。
    *   Optimized command usage specifically for the Hwamai server.
*   **工作方塊介面 (Workstation Menu)**：
*   **Workstation Menu:**
    *   快捷鍵：`Shift + G`
    *   Keybinding: `Shift + G`
    *   快速開啟：工作台、切石機、製圖桌、紡織機、鍛造台、砂輪、垃圾桶 (岩漿桶圖示)、終界箱、鐵砧。
    *   Quickly open: Crafting Table, Stonecutter, Cartography Table, Loom, Smithing Table, Grindstone, Trash Can (Lava Bucket icon), Ender Chest, Anvil.
    *   點擊對應圖示即可輸入指令開啟遠端介面。
    *   Click the corresponding icon to execute commands and open the remote interface.
*   **取得物品介面 (Get Item Menu)**：
*   **Get Item Menu:**
    *   快捷鍵：`G`
    *   Keybinding: `G`
    *   快速獲取常用物品：煙火、透明展示框、箭矢、頭顱、車 (礦車圖示)、鞘翅、夜魅皮膜、光源。
    *   Quickly obtain common items: Fireworks, Invisible Item Frames, Arrows, Player Heads, Minecarts, Elytra, Phantom Membranes, Light Blocks.

### 5. 自定義指令 (Custom Commands)
### 5. Custom Commands
*   **靈活配置**：使用者可以在「指令列表」頁面中，點擊「新增」來建立自己的快捷按鍵。
*   **Flexible Configuration**: Users can create their own shortcut keys by clicking "Add" on the "Command List" page.
*   **按鍵偵測**：支援單鍵、組合鍵 (如 `Ctrl + Shift + Z`)，並可設定為「按下觸發」或「放開觸發」。
*   **Key Detection**: Supports single keys and key combinations (e.g., `Ctrl + Shift + Z`), and can be configured as "Trigger on Press" or "Trigger on Release".

## ⚙️ 安裝與使用 (Installation and Usage)

1.  安裝 **Fabric Loader** (對應 Minecraft 版本 1.21.1)。
    Install **Fabric Loader** (for Minecraft version 1.21.1).
2.  安裝 **Fabric API**。
    Install **Fabric API**.
3.  將本模組 (`hwamaiHelper-x.x.x.jar`) 放入 `.minecraft/mods` 資料夾。
    Place this mod (`hwamaiHelper-x.x.x.jar`) into the `.minecraft/mods` folder.
4.  啟動遊戲，進入伺服器後即可使用。
    Launch the game and join the server to start using it.

## 🛠️ 設定檔 (Configuration)

模組的設定檔位於 `.minecraft/config/hwamaihelper.json`。你可以直接編輯此檔案，或透過遊戲內的「個人設定」介面進行調整。
The mod's configuration file is located at `.minecraft/config/hwamaihelper.json`. You can edit this file directly or adjust settings through the in-game "Personal Settings" interface.

主要設定項包括：
Main configuration items include:
*   `enabled`: 是否啟用模組。 (Whether the mod is enabled.)
*   `autoDisableResourcePack`: 進入伺服器時是否自動發送取消材質包指令。 (Whether to automatically send the cancel resource pack command upon joining.)
*   `autoReplenishFireworks`: 是否自動補充煙火。 (Whether to automatically replenish fireworks.)
*   `openMenuKey`: 主選單快捷鍵 (預設 `X + F`)。 (Main menu keybinding.)
*   `gameModeWheelKey`: 模式切換輪盤快捷鍵 (預設 `alt`)。 (Game mode wheel keybinding.)
*   `entries`: 儲存所有自定義快捷指令的清單。 (List of all custom shortcut commands.)

## 🏗️ 開發與編譯 (Building)

如果你想自行編譯本專案，請確保已安裝 Java 21。
If you want to build this project yourself, ensure you have Java 21 installed.

```bash
# 使用 Gradle 編譯
./gradlew build
```

編譯完成後的檔案將位於 `build/libs` 資料夾下。
The built JAR file will be located in the `build/libs` folder.

---
*本模組為華麥伺服器專用輔助工具，請遵守伺服器規範使用。*
*This mod is an auxiliary tool specifically for the Hwamai server; please follow the server rules while using it.*