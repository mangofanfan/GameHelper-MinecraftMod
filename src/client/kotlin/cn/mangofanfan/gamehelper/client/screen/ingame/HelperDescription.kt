package cn.mangofanfan.gamehelper.client.screen.ingame

import cn.mangofanfan.gamehelper.client.handler.PlayerDeathHandler
import cn.mangofanfan.gamehelper.client.handler.info.DeathPosition
import cn.mangofanfan.gamehelper.client.screen.config.ClientStatus
import cn.mangofanfan.gamehelper.client.screen.config.HelperConfigScreenBuilder
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.InGameScreen
import cn.mangofanfan.gamehelper.client.screen.ingame.widget.DeathPositionItemPanel
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.stat.StatFormatter
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 按下`G`键打开的游戏内帮助屏幕。
 */
@Environment(EnvType.CLIENT)
class HelperDescription(val parent: Screen?) : LightweightGuiDescription() {
    val client: MinecraftClient? = MinecraftClient.getInstance()
    val root = WGridPanel()
    val backButton = FButton(Text.translatable("gamehelper.screen.back_button"))
    val infoDisplayButton = FButton(Text.translatable("gamehelper.screen.info_display.0"))
    val gamerulesButton = FButton(Text.translatable("gamehelper.screen.gamerules_button"))
    val debuggerButton = FButton(Text.translatable("gamehelper.screen.debugger_button"))
    val configScreenButton = FButton(Text.translatable("gamehelper.screen.config_screen_button"))
    val deathPosClearButton = FButton(Text.translatable("gamehelper.screen.death_position_clear_button"))
    var deathPosListWidget: WListPanel<DeathPosition, DeathPositionItemPanel>? = null
    val deathHandler = PlayerDeathHandler.Companion.instance!!
    val deathConfigurator = { deathPosition: DeathPosition, itemPanel: DeathPositionItemPanel ->
        itemPanel.deathPosition = deathPosition
    }

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
            client!!.setScreen(parent)
        }
        infoDisplayButton.setOnClick {
            nextInfoDisplay()
        }
        gamerulesButton.setOnClick {
            client!!.setScreen(InGameScreen(
                GamerulesDescription(),
                client.currentScreen
            ))
        }
        gamerulesButton.addTooltip(Text.translatable("gamehelper.screen.gamerules_description"))
        debuggerButton.setOnClick {
            client!!.setScreen(InGameScreen(
                DebuggerDescription(),
                client.currentScreen
            ))
        }
        debuggerButton.addTooltip(Text.translatable("gamehelper.screen.debugger_description"))
        configScreenButton.setOnClick {
            if (ClientStatus.Status.isClothConfigLoaded)
                client!!.setScreen(HelperConfigScreenBuilder(client.currentScreen!!).build())
            else {
                logger.error("Try opening config screen but Cloth Config is not loaded, please install Cloth Config to use this mod.")
                client!!.player?.sendMessage(
                    Text.translatable("gamehelper.screen.cloth_config_not_loaded").withColor(Colors.RED),
                    false)
            }
        }
        deathPosListWidget = WListPanel(deathHandler.deathPosList, ::DeathPositionItemPanel, deathConfigurator)
        deathPosClearButton.addTooltip(Text.translatable("gamehelper.screen.death_position_clear_button.description"))
        deathPosClearButton.setOnClick {
            deathHandler.clearDeathPos()
            deathPosClearButton.isEnabled = false
            deathPosClearButton.label = Text.translatable("gamehelper.screen.death_position.deleted")
        }
        if (deathHandler.deathPosList.isEmpty()) deathPosClearButton.isEnabled = false

        root.add(backButton, 0, 0, 2, 1)
        root.add(infoDisplayButton, 2, 0, 16, 1)
        root.add(gamerulesButton, 13, 2, 4, 1)
        root.add(debuggerButton, 13, 3, 4, 1)
        root.add(configScreenButton, 13, 10, 4, 1)
        root.add(deathPosClearButton, 0, 2, 12, 1)
        root.add(deathPosListWidget, 0, 3, 12, 8)

        root.validate(this)

        client!!.networkHandler!!.sendPacket(ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS))
    }

    fun nextInfoDisplay() {
        infoId = if (infoId >= 3) 1 else infoId + 1
        val text = when (infoId) {
            1 -> Text.translatable(
                "gamehelper.screen.info_display.1",
                formatter.format(client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME))))
            2 -> Text.translatable(
                "gamehelper.screen.info_display.2",
                client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)))
            3 -> Text.translatable(
                "gamehelper.screen.info_display.3",
                client!!.player!!.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.SLEEP_IN_BED)))
            else -> {Text.translatable("gamehelper.screen.info_display.0")}
        }
        infoDisplayButton.label = text
    }
}