package cn.mangofanfan.gamehelper.client.screen.ingame.widget

import cn.mangofanfan.gamehelper.client.handler.GameRulesHandler
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FLabel
import cn.mangofanfan.gamehelper.config.ConfigManager
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class GameruleIntItemPanel : WPlainPanel()  {
    val logger: Logger = LoggerFactory.getLogger("GameruleIntItemPanel")
    val handler = GameRulesHandler.Companion.getInstance()
    var ruleNameLabel: FLabel? = null
    var ruleIntField: WTextField? = null
    var button: FButton? = null
    var ruleValue: Int? = null
        set(value) {
            if (field != null) {
                // 初始化此Panel时当然不应该执行命令啦
                handler.changeGameRule(rule!!, value!!)
            }
            field = value
            ruleIntField!!.text = value.toString()
        }

    /**
     * 游戏规则名，String。
     */
    var rule: String? = null
        set(ruleName) {
            field = ruleName
            val ruleData = handler.intRuleMap[ruleName]!!
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
        ruleIntField = WTextField()
        ruleIntField!!.maxLength = 10
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
            logger.debug("Change gamerule ${rule!!} to $ruleValue")
        } catch (_: Exception) {
            ruleIntField!!.text = ruleValue.toString()
            logger.warn("Invalid number: ${ruleIntField!!.text}")
        }
    }
}