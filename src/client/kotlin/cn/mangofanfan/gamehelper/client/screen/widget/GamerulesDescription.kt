package cn.mangofanfan.gamehelper.client.screen.widget

import cn.mangofanfan.gamehelper.client.screen.libgui.GameruleBooleanItemPanel
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.world.GameRules
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class GamerulesBooleanDescription(parent: Screen?): LightweightGuiDescription() {
    val _parent: Screen? = parent
    val _client: MinecraftClient? = MinecraftClient.getInstance()
    val logger: Logger = LoggerFactory.getLogger("InGameGamerules")
    val root = WGridPanel()
    val backButton = WButton(Text.translatable("gamehelper.screen.back_button"))
    val titleLabel: WLabel = WLabel(Text.translatable("gamehelper.screen.gamerules_title"))
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setVerticalAlignment(VerticalAlignment.CENTER)
    val descriptionLabel: WLabel = WLabel(Text.translatable("gamehelper.screen.gamerules_description"))
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setVerticalAlignment(VerticalAlignment.CENTER)
    var gamerulesListWidget: WListPanel<GameRules.Key<GameRules.BooleanRule>, GameruleBooleanItemPanel>? = null

    // gamerules
    val gamerulesConfigurator = { gamerule: GameRules.Key<GameRules.BooleanRule>, itemPanel: GameruleBooleanItemPanel ->
        // 在GameruleItemPanel的rule的setter中设计了封装
        itemPanel.rule = gamerule
    }
    val gamerulesBoolean = listOf(
        GameRules.DO_FIRE_TICK,
        GameRules.ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER,
        GameRules.DO_MOB_GRIEFING,
        GameRules.KEEP_INVENTORY,
        GameRules.DO_MOB_SPAWNING,
        GameRules.DO_MOB_LOOT,
        GameRules.PROJECTILES_CAN_BREAK_BLOCKS,
        GameRules.DO_TILE_DROPS,
        GameRules.DO_ENTITY_DROPS,
        GameRules.COMMAND_BLOCK_OUTPUT,
        GameRules.NATURAL_REGENERATION,
        GameRules.DO_DAYLIGHT_CYCLE,
        GameRules.LOG_ADMIN_COMMANDS,
        GameRules.SHOW_DEATH_MESSAGES,
        GameRules.SEND_COMMAND_FEEDBACK,
        GameRules.REDUCED_DEBUG_INFO,
        GameRules.SPECTATORS_GENERATE_CHUNKS,
        GameRules.DISABLE_PLAYER_MOVEMENT_CHECK,
        GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK,
        GameRules.DO_WEATHER_CYCLE,
        GameRules.DO_LIMITED_CRAFTING,
        GameRules.ANNOUNCE_ADVANCEMENTS,
        GameRules.DISABLE_RAIDS,
        GameRules.DO_INSOMNIA,
        GameRules.DO_IMMEDIATE_RESPAWN,
        GameRules.DROWNING_DAMAGE,
        GameRules.FALL_DAMAGE,
        GameRules.FIRE_DAMAGE,
        GameRules.FREEZE_DAMAGE,
        GameRules.DO_PATROL_SPAWNING,
        GameRules.DO_TRADER_SPAWNING,
        GameRules.DO_WARDEN_SPAWNING,
        GameRules.FORGIVE_DEAD_PLAYERS,
        GameRules.UNIVERSAL_ANGER,
        GameRules.BLOCK_EXPLOSION_DROP_DECAY,
        GameRules.MOB_EXPLOSION_DROP_DECAY,
        GameRules.TNT_EXPLOSION_DROP_DECAY,
        GameRules.WATER_SOURCE_CONVERSION,
        GameRules.LAVA_SOURCE_CONVERSION,
        GameRules.GLOBAL_SOUND_EVENTS,
        GameRules.DO_VINES_SPREAD,
        GameRules.ENDER_PEARLS_VANISH_ON_DEATH,
        GameRules.TNT_EXPLODES,
        GameRules.LOCATOR_BAR,
        GameRules.PVP,
        GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS,
        GameRules.SPAWN_MONSTERS,
        GameRules.COMMAND_BLOCKS_ENABLED,
        GameRules.SPAWNER_BLOCKS_ENABLED
    )

    init {
        setRootPanel(root)
        root.setSize(256, 180)
        root.setInsets(Insets.ROOT_PANEL)

        backButton.setOnClick {
            _client!!.setScreen(_parent)
        }
        gamerulesListWidget = WListPanel(gamerulesBoolean, ::GameruleBooleanItemPanel, gamerulesConfigurator)
        gamerulesListWidget!!.setListItemHeight(18)
        gamerulesListWidget!!.validate(this)
        logger.info("gamerulesListWidget: ${gamerulesBoolean.size}")

        root.add(backButton, 0, 0, 4, 1)
        root.add(titleLabel, 4, 0, 14, 1)
        root.add(descriptionLabel, 0, 1, 18, 1)
        root.add(gamerulesListWidget, 0, 2, 18, 7)

        root.validate(this)
    }
}