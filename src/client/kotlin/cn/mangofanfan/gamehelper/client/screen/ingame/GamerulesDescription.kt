package cn.mangofanfan.gamehelper.client.screen.ingame

import cn.mangofanfan.gamehelper.client.handler.GameRulesHandler
import cn.mangofanfan.gamehelper.client.screen.ingame.widget.GameruleBooleanItemPanel
import cn.mangofanfan.gamehelper.client.screen.ingame.widget.GameruleIntItemPanel
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.WTabPanel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import net.minecraft.world.GameRules

/**
 * 在游戏内编辑gamerules的屏幕。
 */
@Environment(EnvType.CLIENT)
class GamerulesDescription: LightweightGuiDescription() {
    private val handler = GameRulesHandler.Companion.getInstance()

    var root = WTabPanel()

    var gamerulesBooleanListWidget: WListPanel<String, GameruleBooleanItemPanel>? = null
    var gamerulesIntListWidget: WListPanel<String, GameruleIntItemPanel>? = null

    // gamerules
    val gamerulesBooleanConfigurator = { ruleName: String, itemPanel: GameruleBooleanItemPanel ->
        itemPanel.rule = ruleName
    }
    val gamerulesIntConfigurator = { ruleName: String, itemPanel: GameruleIntItemPanel ->
        itemPanel.rule = ruleName
    }

    init {
        setRootPanel(root)
        root.setSize(256, 180)
        println(handler.booleanRuleMap.keys)
        println(handler.intRuleMap.keys)
        gamerulesBooleanListWidget = WListPanel(handler.booleanRuleMap.keys.toList(),
            ::GameruleBooleanItemPanel, gamerulesBooleanConfigurator)
        gamerulesBooleanListWidget!!.setListItemHeight(18)
        gamerulesBooleanListWidget!!.parent = root
        gamerulesBooleanListWidget!!.setSize(240, 180)
        gamerulesIntListWidget = WListPanel(handler.intRuleMap.keys.toList(),
            ::GameruleIntItemPanel, gamerulesIntConfigurator)
        gamerulesIntListWidget!!.setListItemHeight(18)
        gamerulesIntListWidget!!.parent = root
        gamerulesIntListWidget!!.setSize(240, 180)
        root.add(gamerulesBooleanListWidget) { builder ->
            builder.title(Text.translatable("gamehelper.screen.gamerules_tab.boolean"))
        }
        root.add(gamerulesIntListWidget) { builder ->
            builder.title(Text.translatable("gamehelper.screen.gamerules_tab.int"))
        }

        root.validate(this)
    }
}