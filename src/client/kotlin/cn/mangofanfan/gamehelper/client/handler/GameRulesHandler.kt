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

/**
 * **客户端专属的 Handler**。
 *
 * 负责在单人和多人游戏环境中处理 Minecraft 游戏规则的存储、获取和修改。
 *
 * 此类负责维护布尔值和整数类型游戏规则的状态，根据当前环境初始化游戏规则，
 * 并提供机制来更新和同步客户端与服务器之间的游戏规则。
 *
 * @constructor 根据提供的服务器环境初始化 GameRulesHandler 实例。
 *
 * @param server MinecraftServer 实例（可选）。如果提供了该参数，则以单人游戏模式初始化处理程序。
 * 如果为空，则假定为多人游戏模式并从服务器请求游戏规则数据。
 */
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
     * 在单人游戏中，使用此方法通过传入的[MinecraftServer]实例获取所有游戏规则。
     *
     * 需要传入[MinecraftServer]实例。
     *
     * 由于多人游戏中无法从[MinecraftClient]实例获取[MinecraftServer]实例，因此在多人游戏/服务器环境中需使用[updateGameRulesInMultiPlayer]。
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
     * 在多人游戏中，使用此方法向服务端发送数据包以请求发送全部游戏规则，然后由服务端发送游戏规则信息。
     *
     * 此方法在单人游戏中也可行，但没有必要，因为单人游戏可以直接获取到[MinecraftServer]实例，参见[updateGameRulesInSinglePlayer]方法。
     */
    fun updateGameRulesInMultiPlayer() {
        ClientPlayNetworking.send(RequestGameruleC2SPayload("ALL"))
    }

    /**
     * 在多人游戏中，更新客户端模组保存的游戏规则数据的方法。
     *
     * **此方法不改变实际游戏规则，仅用作接收到服务端回复的游戏规则数据包时将其保存在客户端的模组数据中。**
     *
     * @param ruleName 规则名称
     * @param value 规则值，可以为[Boolean]或[Int]
     * @param translationKey 规则翻译键
     */
    fun updateGameRuleInMultiPlayer(ruleName: String, value: Boolean, translationKey: String) {
        booleanRuleMap[ruleName] = BooleanRuleData(translationKey, value)
    }

    /**
     * 在多人游戏中，更新客户端模组保存的游戏规则数据的方法。
     *
     * **此方法不改变实际游戏规则，仅用作接收到服务端回复的游戏规则数据包时将其保存在客户端的模组数据中。**
     *
     * @param ruleName 规则名称
     * @param value 规则值，可以为[Boolean]或[Int]
     * @param translationKey 规则翻译键
     */
    fun updateGameRuleInMultiPlayer(ruleName: String, value: Int, translationKey: String) {
        intRuleMap[ruleName] = IntRuleData(translationKey, value)
    }

    /**
     * 使用此方法更改游戏规则，在单人游戏中与多人游戏中均可以。
     *
     * 修改游戏规则的原理是执行`gamerule`指令，位于[runGameRuleCommand]方法中。
     *
     * **不检查玩家是否有权限修改游戏规则，应当在调用前检查。**
     */
    fun changeGameRule(ruleName: String, value: Any) {
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

    /**
     * 发送`gamerule`指令以修改游戏规则，接收规则名与值，其中值需要为[Int]或[Boolean]，这是Minecraft游戏规则的要求。
     *
     * 单人游戏与多人游戏均可使用此方法，因为其本质是发送指令，无需服务端额外处理。
     *
     * **仅负责发送指令，无法检查执行结果，应当在执行之前手动检查玩家是否有权限。**
     */
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