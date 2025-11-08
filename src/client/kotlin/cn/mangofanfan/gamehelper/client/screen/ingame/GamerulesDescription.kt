package cn.mangofanfan.gamehelper.client.screen.ingame

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

    var root = WTabPanel()

    var gamerulesBooleanListWidget: WListPanel<GameRules.Key<GameRules.BooleanRule>, GameruleBooleanItemPanel>? = null
    var gamerulesIntListWidget: WListPanel<GameRules.Key<GameRules.IntRule>, GameruleIntItemPanel>? = null

    // gamerules
    val gamerulesBooleanConfigurator = { gamerule: GameRules.Key<GameRules.BooleanRule>, itemPanel: GameruleBooleanItemPanel ->
        itemPanel.rule = gamerule
    }
    val gamerulesIntConfigurator = { gamerule: GameRules.Key<GameRules.IntRule>, itemPanel: GameruleIntItemPanel ->
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
    val gamerulesInt = listOf(
        GameRules.RANDOM_TICK_SPEED,
        GameRules.SPAWN_RADIUS,
        GameRules.MAX_ENTITY_CRAMMING,
        GameRules.MAX_COMMAND_CHAIN_LENGTH,
        GameRules.MAX_COMMAND_FORK_COUNT,
        GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT,
        GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY,
        GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY,
        GameRules.PLAYERS_SLEEPING_PERCENTAGE,
        GameRules.SNOW_ACCUMULATION_HEIGHT,
        GameRules.MINECART_MAX_SPEED  // Minecraft Max Speed可能获取失败，已经在 GameruleIntItemPanel 中针对获取失败进行额外处理。
    )

    init {
        setRootPanel(root)
        root.setSize(256, 180)

        gamerulesBooleanListWidget = WListPanel(gamerulesBoolean,
            ::GameruleBooleanItemPanel, gamerulesBooleanConfigurator)
        gamerulesBooleanListWidget!!.setListItemHeight(18)
        gamerulesBooleanListWidget!!.parent = root
        gamerulesBooleanListWidget!!.setSize(240, 180)
        gamerulesIntListWidget = WListPanel(gamerulesInt, ::GameruleIntItemPanel, gamerulesIntConfigurator)
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