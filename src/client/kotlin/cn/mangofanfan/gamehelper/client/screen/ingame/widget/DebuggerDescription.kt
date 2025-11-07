package cn.mangofanfan.gamehelper.client.screen.ingame.widget

import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FLabel
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import net.minecraft.util.Colors

@Environment(EnvType.CLIENT)
class DebuggerDescription : LightweightGuiDescription() {
    val root = WGridPanel()
    var debuggerListWidget: WListPanel<DebuggerItem, DebuggerItemPanel>? = null

    /**
     * 调试组合键和其描述文本的结构
     */
    class DebuggerItem(val key: String, val text: Text)

    /**
     * 调试组合键的列表元素面板
     */
    class DebuggerItemPanel : WPlainPanel() {
        var keyButton: FButton? = null
        var descriptionLabel: FLabel? = null
        var debuggerItem: DebuggerItem? = null
            set(value) {
                field = value
                keyButton!!.label = Text.literal(value!!.key).withColor(Colors.YELLOW)
                descriptionLabel!!.text = value.text
            }
        init {
            setSize(256, 18)
            keyButton = FButton(Text.literal("Key"))
            keyButton!!.setEnabled(false)
            descriptionLabel = FLabel(Text.literal("Tip"))
            descriptionLabel!!.setVerticalAlignment(VerticalAlignment.CENTER)
            descriptionLabel!!.setHorizontalAlignment(HorizontalAlignment.CENTER)
            add(keyButton, 0, 0, 50, 18)
            add(descriptionLabel, 60, 0, 190, 18)
        }
    }

    /**
     * 调试组合键列表
     */
    val debuggerList = listOf(
        DebuggerItem("F3 + C", Text.translatable("gamehelper.screen.debugger.key.F3C")),
        DebuggerItem("F3 + D", Text.translatable("gamehelper.screen.debugger.key.F3D")),
        DebuggerItem("F3 + G", Text.translatable("gamehelper.screen.debugger.key.F3G")),
        DebuggerItem("F3 + H", Text.translatable("gamehelper.screen.debugger.key.F3H")),
        DebuggerItem("F3 + T", Text.translatable("gamehelper.screen.debugger.key.F3T")),
        DebuggerItem("F3 + F4", Text.translatable("gamehelper.screen.debugger.key.F3F4")),
        DebuggerItem("F3 + F6", Text.translatable("gamehelper.screen.debugger.key.F3F6")),
        DebuggerItem("F3 + Q", Text.translatable("gamehelper.screen.debugger.key.F3Q"))
    )

    /**
     * 配置器
     */
    val configurator = { debuggerItem: DebuggerItem, itemPanel: DebuggerItemPanel ->
        itemPanel.debuggerItem = debuggerItem
    }

    init {
        setRootPanel(root)
        root.setSize(256, 180)

        debuggerListWidget = WListPanel(debuggerList, ::DebuggerItemPanel, configurator)
        debuggerListWidget!!.setListItemHeight(18)

        root.add(
            FLabel(Text.translatable("gamehelper.screen.debugger.key_description.1"))
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER),
            0, 0, 16, 1)
        root.add(
            FLabel(Text.translatable("gamehelper.screen.debugger.key_description.2"))
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER),
            0, 1, 16, 1)
        root.add(debuggerListWidget, 0, 2, 16, 8)

        root.validate(this)
    }
}