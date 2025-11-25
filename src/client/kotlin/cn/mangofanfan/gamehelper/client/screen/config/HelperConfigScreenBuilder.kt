package cn.mangofanfan.gamehelper.client.screen.config

import cn.mangofanfan.gamehelper.config.ConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * 本模组的配置屏幕Builder，使用Cloth config api
 */
@Environment(EnvType.CLIENT)
class HelperConfigScreenBuilder(parent: Screen) {
    val manager = ConfigManager.getInstance()
    /**
     * ConfigBuilder
     */
    val configBuilder: ConfigBuilder = ConfigBuilder.create()

    /**
     * GUI与单人游戏配置类
     */
    val singleplayerCategory: ConfigCategory = configBuilder.getOrCreateCategory(Text.translatable("gamehelper.screen.config.category.singleplayer"))
    /**
     * 服务器与多人游戏配置类
     */
    val multiplayerCategory: ConfigCategory = configBuilder.getOrCreateCategory(Text.translatable("gamehelper.screen.config.category.multiplayer"))
    /**
     * ConfigEntryBuilder
     */
    val entryBuilder: ConfigEntryBuilder = configBuilder.entryBuilder()

    init {
        configBuilder.title = Text.translatable("gamehelper.screen.config.title")
        configBuilder.parentScreen = parent
        singleplayerCategory.addEntry(
            entryBuilder.startTextDescription(
                Text.translatable("gamehelper.screen.config.category.singleplayer.description")
            ).build()
        )
        singleplayerCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.showGameruleTranslationInGUI"),
                manager.config.showGameruleTranslationInGUI)
                .setDefaultValue(manager.config.showGameruleTranslationInGUI)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.showGameruleTranslationInGUI.description"))
                .setSaveConsumer { manager.config.showGameruleTranslationInGUI = it }
                .build()
        )
        singleplayerCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.showMoreInfoInGame"),
                manager.config.showMoreInfoInGame)
                .setDefaultValue(manager.config.showMoreInfoInGame)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.showMoreInfoInGame.description"))
                .setSaveConsumer { manager.config.showMoreInfoInGame = it }
                .build()
        )
        multiplayerCategory.addEntry(
            entryBuilder.startTextDescription(
                Text.translatable("gamehelper.screen.config.category.multiplayer.description")
            ).build()
        )
        multiplayerCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.recordDeathPosition"),
                manager.config.recordDeathPosition)
                .setDefaultValue(manager.config.recordDeathPosition)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.recordDeathPosition.description"))
                .setSaveConsumer { manager.config.recordDeathPosition = it }
                .build()
        )
        multiplayerCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.enableGameRulesManager"),
                manager.config.enableGameRulesManager)
                .setDefaultValue(manager.config.enableGameRulesManager)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.enableGameRulesManager.description"))
                .setSaveConsumer { manager.config.enableGameRulesManager = it }
                .build()
        )
        multiplayerCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.disableGameRulesForAnyone"),
                manager.config.disableGameRulesForAnyone)
                .setDefaultValue(manager.config.disableGameRulesForAnyone)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.disableGameRulesForAnyone.description"))
                .setSaveConsumer { manager.config.disableGameRulesForAnyone = it }
                .build()
        )
        configBuilder.setSavingRunnable { manager.saveConfig() }
    }

    /**
     * build并返回配置屏幕
     */
    fun build(): Screen {
        return configBuilder.build()
    }
}