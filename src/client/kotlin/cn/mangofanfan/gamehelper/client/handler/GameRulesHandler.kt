package cn.mangofanfan.gamehelper.client.handler

import cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.server.MinecraftServer
import net.minecraft.world.GameRules
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class GameRulesHandler(server: MinecraftServer?) {
    /**
     * 布尔值类型游戏规则存储字典
     */
    val booleanRuleMap: MutableMap<String, BooleanRuleData> = mutableMapOf()

    /**
     * 整型类型游戏规则存储字典
     */
    val intRuleMap: MutableMap<String, IntRuleData> = mutableMapOf()

    private val client = MinecraftClient.getInstance()!!

    private val logger: Logger = LoggerFactory.getLogger(GameRulesHandler::class.java)

    init {
        // 如果当前正在服务器/多人游戏中游玩，则需要向服务端发送请求获取游戏规则。
        if (server == null && !client.isInSingleplayer) {
            updateGameRulesInMultiPlayer()
            logger.info("GameRulesHandler init in multiplayer")
        }
        // 如果在单人游戏中，则可以直接遍历 `server.gameRules` 获取所有的游戏规则
        else if (server != null && client.isInSingleplayer) {
            updateGameRulesInSinglePlayer(server)
            logger.info("GameRulesHandler init in singleplayer")
        }
        else {
            logger.error("GameRulesHandler init error")
            throw RuntimeException("Unknown running environment: MinecraftClient.getInstance().isInSingleplayer ="
                    + client.isInSingleplayer
                    + " server = "
                    + server.toString())
        }
    }

    /**
     * 在单人游戏中，使用此方法直接从客户端实例获取服务端[MinecraftServer]实例，并遍历获取所有游戏规则。
     *
     * 需要传入[MinecraftServer]实例。
     */
    fun updateGameRulesInSinglePlayer(server: MinecraftServer) {
        server.gameRules.accept(object: GameRules.Visitor {
            override fun visitInt(
                key: GameRules.Key<GameRules.IntRule>,
                type: GameRules.Type<GameRules.IntRule>
            ) {
                val value = server.gameRules.getInt(key)
                intRuleMap[key.name] = IntRuleData(key.translationKey, value)
                logger.debug("GameRules IntRule ${key.name}: $value")
            }

            override fun visitBoolean(
                key: GameRules.Key<GameRules.BooleanRule>,
                type: GameRules.Type<GameRules.BooleanRule>
            ) {
                val value = server.gameRules.getBoolean(key)
                booleanRuleMap[key.name] = BooleanRuleData(key.translationKey, value)
                logger.debug("GameRules BooleanRule ${key.name}: $value")
            }
        })
    }

    /**
     * 在多人游戏中，使用此方法向服务端发送数据包以请求发送游戏规则，然后由服务端发送游戏规则信息。
     *
     * 此方法在单人游戏中也可行，但没有必要，因为单人游戏可以直接获取到[MinecraftServer]实例。
     */
    fun updateGameRulesInMultiPlayer() {
        ClientPlayNetworking.send(RequestGameruleC2SPayload("ALL"))
    }

    fun updateGameRuleInMultiPlayer(ruleName: String, value: Boolean, translationKey: String) {
        booleanRuleMap[ruleName] = BooleanRuleData(translationKey, value)
    }

    fun updateGameRuleInMultiPlayer(ruleName: String, value: Int, translationKey: String) {
        intRuleMap[ruleName] = IntRuleData(translationKey, value)
    }

    fun changeGameRuleInSinglePlayer(ruleName: String, value: Any) {
        when (value) {
            is Boolean -> {
                booleanRuleMap[ruleName]?.value = value
                runGameRuleCommand(ruleName, value)
            }
            is Int -> {
                intRuleMap[ruleName]?.value = value
                runGameRuleCommand(ruleName, value)
            }
            else -> throw RuntimeException("Unknown game rule value type: $value")
        }
        logger.info("Change gamerule $ruleName to $value")
    }

    fun runGameRuleCommand(ruleName: String, value: Any) {
        client.networkHandler!!.sendChatCommand("gamerule $ruleName $value")
    }

    class IntRuleData(val translationKey: String, var value: Int)
    class BooleanRuleData(val translationKey: String, var value: Boolean)

    object Companion {
        @JvmField
        var instance: GameRulesHandler? = null

        fun getInstance(): GameRulesHandler {
            if (instance == null) {
                throw RuntimeException("GameRulesHandler instance is null")
            }
            return instance!!
        }

        fun build(server: MinecraftServer?): GameRulesHandler {
            if (instance == null) {
                instance = GameRulesHandler(server)
            }
            return instance!!
        }

        fun delete() {
            instance = null
        }
    }
}