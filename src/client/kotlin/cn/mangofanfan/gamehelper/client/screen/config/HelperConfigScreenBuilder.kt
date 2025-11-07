package cn.mangofanfan.gamehelper.client.screen.config

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
     * 总配置类，由于只有一个所以实际不显示
     */
    val generalCategory: ConfigCategory = configBuilder.getOrCreateCategory(Text.translatable("gamehelper.screen.config.category.general"))

    /**
     * ConfigEntryBuilder
     */
    val entryBuilder: ConfigEntryBuilder = configBuilder.entryBuilder()

    init {
        configBuilder.title = Text.translatable("gamehelper.screen.config.title")
        configBuilder.parentScreen = parent
        generalCategory.addEntry(
            entryBuilder.startBooleanToggle(
                Text.translatable("gamehelper.screen.config.entry.showGameruleTranslationInGUI"),
                manager.config.showGameruleTranslationInGUI)
                .setDefaultValue(manager.config.showGameruleTranslationInGUI)
                .setTooltip(Text.translatable("gamehelper.screen.config.entry.showGameruleTranslationInGUI.description"))
                .setSaveConsumer { manager.config.showGameruleTranslationInGUI = it }
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