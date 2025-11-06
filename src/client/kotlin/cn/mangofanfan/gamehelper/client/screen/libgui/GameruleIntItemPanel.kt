package cn.mangofanfan.gamehelper.client.screen.libgui

import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.world.GameRules
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

@Environment(EnvType.CLIENT)
class GameruleIntItemPanel : WPlainPanel ()  {
    val logger: Logger = LoggerFactory.getLogger("GameruleIntItemPanel")
    var ruleNameLabel: FLabel? = null
    var ruleIntField: WTextField? = null
    var button: FButton? = null
    var ruleValue: Int? = null
        set(value) {
            if (field != null) {
                // 初始化此Panel时当然不应该执行命令啦
                MinecraftClient.getInstance().networkHandler!!.sendChatCommand(
                    "gamerule ${rule!!.name} $value"
                )
            }
            field = value
            ruleIntField!!.text = value.toString()
        }

    var rule: GameRules.Key<GameRules.IntRule>? = null
        set(value) {
            field = value
            ruleNameLabel!!.text = Text.literal(value!!.name)
            ruleNameLabel!!.addTooltip(Text.translatable(value.translationKey))
            try {
                ruleValue = MinecraftClient.getInstance().server!!.gameRules.getInt(value)
            } catch (e: IllegalArgumentException) {
                ruleValue = 10721
                ruleIntField!!.setEditable(false)
                button!!.setEnabled(false)
                button!!.addTooltip(Text.translatable("gamehelper.screen.gamerules_tab.disabled_gamerule"))
            }
        }

    init {
        setSize(256, 18)
        ruleNameLabel = FLabel(Text.literal("TvT"))
        ruleNameLabel!!.setVerticalAlignment(VerticalAlignment.CENTER)
        ruleIntField = WTextField()
        ruleIntField!!.setMaxLength(10)
        ruleIntField!!.text = "0"
        button = FButton(Text.translatable("gamehelper.screen.gamerules_tab.int.save"))
        button!!.setOnClick { onChanged() }
        add(ruleNameLabel, 0, 0, 140, 18)
        add(ruleIntField, 140, 0, 60, 18)
        add(button, 200, 0, 30, 18)
    }

    fun onChanged() {
        try {
            ruleValue = ruleIntField!!.text.toInt()
            logger.info("Change gamerule ${rule!!.name} to $ruleValue")
        } catch (e: Exception) {
            ruleIntField!!.text = ruleValue.toString()
            logger.warn("Invalid number: ${ruleIntField!!.text}")
        }
    }
}