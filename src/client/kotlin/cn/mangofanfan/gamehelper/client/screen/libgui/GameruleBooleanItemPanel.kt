package cn.mangofanfan.gamehelper.client.screen.libgui

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.world.GameRules
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameruleBooleanItemPanel : WPlainPanel () {
    val logger: Logger = LoggerFactory.getLogger("GameruleBooleanItemPanel")
    var ruleNameLabel: WLabel? = null
    var ruleToggleButton: WButton? = null
    var ruleValue: Boolean? = null
        set(value) {
            // 执行gamerule命令
            // 花了好久好久才终于从源码里找到这个执行命令的方法！！理论上只要有权限，在单人游戏中和服务器里都可以执行~
            if (field != null) {
                // 初始化此Panel时当然不应该执行命令啦
                MinecraftClient.getInstance().networkHandler!!.sendChatCommand(
                    "gamerule ${rule!!.name} ${if (value == true) "true" else "false"}"
                )
            }
            // TODO：检查命令是否执行成功，如果没有成功则不应该更改状态
            field = value
            ruleToggleButton!!.label = Text.literal(if (value == true) "TRUE" else "FALSE")
                .withColor(if (value == true) 0x00FF00 else 0xFF0000)
        }

    // 设置 gamerule
    var rule: GameRules.Key<GameRules.BooleanRule>? = null
        set(value) {
            field = value
            ruleNameLabel!!.text = Text.translatable(value!!.translationKey)
            ruleNameLabel!!.addTooltip(TooltipBuilder().add(Text.literal(value.name)))
            ruleValue = MinecraftClient.getInstance().server!!.gameRules.getBoolean(value)
        }

    init {
        setSize(256, 18)
        ruleNameLabel = WLabel(Text.literal("TvT"))
        ruleNameLabel!!.setVerticalAlignment(VerticalAlignment.CENTER)
        ruleToggleButton = WButton(Text.literal("OvO"))
        ruleToggleButton!!.setOnClick { toggle() }

        add(ruleNameLabel, 0, 0, 200, 18)
        add(ruleToggleButton, 200, 0, 50, 18)
    }

    fun toggle() {
        if (ruleValue != null){
            ruleValue = !ruleValue!!
            logger.info("Change gamerule ${rule!!.name} to $ruleValue")
        }
        else {
            logger.warn("Gamerule value is null?")
        }
    }
}