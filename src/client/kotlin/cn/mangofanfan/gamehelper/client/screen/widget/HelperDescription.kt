package cn.mangofanfan.gamehelper.client.screen.widget

import cn.mangofanfan.gamehelper.client.screen.libgui.InGameScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class InGameHelperDescription(parent: Screen?) : LightweightGuiDescription() {
    val _parent: Screen? = parent
    val _client: MinecraftClient? = MinecraftClient.getInstance()
    val root = WGridPanel()
    val backButton = WButton(Text.translatable("gamehelper.screen.back_button"))
    val infoDisplayButton = WButton(Text.translatable("gamehelper.screen.info_display.0"))
    val gamerulesButton = WButton(Text.translatable("gamehelper.screen.gamerules_button"))

    // 显示的信息条目
    var infoId = 0

    val logger: Logger = LoggerFactory.getLogger("InGameHelper")

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
                InGameBooleanGamerulesDescription(_client.currentScreen),
                _client.currentScreen
            ))
        }

        root.add(backButton, 0, 0, 4, 1)
        root.add(infoDisplayButton, 4, 0, 14, 1)
        root.add(gamerulesButton, 6, 2, 6, 1)

        root.validate(this)
    }

    fun nextInfoDisplay() {
        infoId = if (infoId >= 3) 1 else infoId + 1
        val text = when (infoId) {
            1 -> Text.translatable("gamehelper.screen.info_display.1", 1)
            2 -> Text.translatable("gamehelper.screen.info_display.2", 2)
            3 -> Text.translatable("gamehelper.screen.info_display.3", 3)
            else -> {Text.translatable("gamehelper.screen.info_display.0")}
        }
        infoDisplayButton.label = text
    }
}