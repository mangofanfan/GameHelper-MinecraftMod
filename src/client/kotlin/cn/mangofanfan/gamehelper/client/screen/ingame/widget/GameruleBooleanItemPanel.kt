package cn.mangofanfan.gamehelper.client.screen.ingame.widget

import cn.mangofanfan.gamehelper.client.handler.GameRulesHandler
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FLabel
import cn.mangofanfan.gamehelper.config.ConfigManager
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class GameruleBooleanItemPanel : WPlainPanel() {
    val logger: Logger = LoggerFactory.getLogger("GameruleBooleanItemPanel")
    val handler = GameRulesHandler.Companion.getInstance()
    var ruleNameLabel: FLabel? = null
    var ruleToggleButton: FButton? = null
    var ruleValue: Boolean? = null
        set(value) {
            // 执行gamerule命令
            // 花了好久好久才终于从源码里找到这个执行命令的方法！！理论上只要有权限，在单人游戏中和服务器里都可以执行~
            if (field != null) {
                // 初始化此Panel时当然不应该执行命令啦
                handler.changeGameRuleInSinglePlayer(rule!!, value!!)
            }
            // TODO：检查命令是否执行成功，如果没有成功则不应该更改状态
            field = value
            ruleToggleButton!!.label = Text.literal(if (value == true) "TRUE" else "FALSE")
                .withColor(if (value == true) Colors.GREEN else Colors.RED)
        }

    /**
     * 游戏规则名，String。
     */
    var rule: String? = null
        set(ruleName) {
            field = ruleName
            val ruleData = handler.booleanRuleMap[ruleName]!!
            ruleValue = ruleData.value
            // 根据配置决定显示模式
            if (ConfigManager.getInstance().config.showGameruleTranslationInGUI) {
                ruleNameLabel!!.text = Text.translatable(ruleData.translationKey)
                ruleNameLabel!!.addTooltip(Text.literal(ruleName))
            } else {
                ruleNameLabel!!.text = Text.literal(ruleName)
                ruleNameLabel!!.addTooltip(Text.translatable(ruleData.translationKey))
            }
        }

    init {
        setSize(256, 18)
        ruleNameLabel = FLabel(Text.literal("TvT"))
        ruleNameLabel!!.setVerticalAlignment(VerticalAlignment.CENTER)
        ruleToggleButton = FButton(Text.literal("OvO"))
        ruleToggleButton!!.setOnClick { toggle() }

        add(ruleNameLabel, 0, 0, 180, 18)
        add(ruleToggleButton, 180, 0, 50, 18)
    }

    fun toggle() {
        if (ruleValue != null){
            ruleValue = !ruleValue!!
            logger.debug("Change gamerule ${rule!!} to $ruleValue")
        }
        else {
            logger.warn("Gamerule value is null?")
        }
    }
}