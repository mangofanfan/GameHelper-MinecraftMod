package cn.mangofanfan.gamehelper.client.screen.widget

import cn.mangofanfan.gamehelper.client.screen.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.libgui.InGameScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.stat.StatFormatter
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 按下`G`键打开的游戏内帮助屏幕。
 */
@Environment(EnvType.CLIENT)
class HelperDescription(parent: Screen?) : LightweightGuiDescription() {
    val _parent: Screen? = parent
    val _client: MinecraftClient? = MinecraftClient.getInstance()
    val root = WGridPanel()
    val backButton = FButton(Text.translatable("gamehelper.screen.back_button"))
    val infoDisplayButton = FButton(Text.translatable("gamehelper.screen.info_display.0"))
    val gamerulesButton = FButton(Text.translatable("gamehelper.screen.gamerules_button"))
    val debuggerButton = FButton(Text.translatable("gamehelper.screen.debugger_button"))

    // 显示的信息条目
    var infoId = 0

    val logger: Logger = LoggerFactory.getLogger("InGameHelper")

    /**
     * 用于语义化统计信息中的游戏时间的formatter
     */
    val formatter: StatFormatter = StatFormatter.TIME

    init {
        logger.info("InGameHelperScreen init")
        setRootPanel(root)
        root.setSize(256, 180)
        root.setInsets(Insets.ROOT_PANEL)

        backButton.setOnClick {
            _client!!.setScreen(_parent)
        }
        infoDisplayButton.setOnClick {
            nextInfoDisplay()
        }
        gamerulesButton.setOnClick {
            _client!!.setScreen(InGameScreen(
                GamerulesDescription(),
                _client.currentScreen
            ))
        }
        gamerulesButton.addTooltip(Text.translatable("gamehelper.screen.gamerules_description"))
        debuggerButton.setOnClick {
            _client!!.setScreen(InGameScreen(
                DebuggerDescription(),
                _client.currentScreen
            ))
        }
        debuggerButton.addTooltip(Text.translatable("gamehelper.screen.debugger_description"))

        root.add(backButton, 0, 0, 2, 1)
        root.add(infoDisplayButton, 2, 0, 16, 1)
        root.add(gamerulesButton, 6, 2, 6, 1)
        root.add(debuggerButton, 6, 3, 6, 1)

        root.validate(this)

        _client!!.networkHandler!!.sendPacket(ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS))
    }

    fun nextInfoDisplay() {
        infoId = if (infoId >= 3) 1 else infoId + 1
        val text = when (infoId) {
            1 -> Text.translatable(
                "gamehelper.screen.info_display.1",
                formatter.format(_client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME))))
            2 -> Text.translatable(
                "gamehelper.screen.info_display.2",
                _client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)))
            3 -> Text.translatable(
                "gamehelper.screen.info_display.3",
                _client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.SLEEP_IN_BED)))
            else -> {Text.translatable("gamehelper.screen.info_display.0")}
        }
        infoDisplayButton.label = text
    }
}