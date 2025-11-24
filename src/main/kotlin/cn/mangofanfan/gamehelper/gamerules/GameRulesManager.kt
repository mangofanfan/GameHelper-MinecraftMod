package cn.mangofanfan.gamehelper.gamerules

import cn.mangofanfan.gamehelper.packet.ResponseGameruleBooleanS2CPayload
import cn.mangofanfan.gamehelper.packet.ResponseGameruleIntS2CPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameRules
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * `GamerulesManager`是只在服务端运行的类型，是单例模式，在服务器启动时更新所有数据。
 */
class GameRulesManager(val gamerules: GameRules) {
    private val logger: Logger = LoggerFactory.getLogger(GameRulesManager::class.java)

    fun getValue(gameruleName: String): Pair<Any, String> {
        var result: Any?
        var value: Any? = null
        var translationKey: String? = null
        gamerules.accept(object: GameRules.Visitor {
            override fun <T : GameRules.Rule<T?>> visit(key: GameRules.Key<T?>, type: GameRules.Type<T?>) {
                if (gameruleName == key.toString()) {
                    result = gamerules.get(key)
                    when (result) {
                        is GameRules.BooleanRule -> value = (result as GameRules.BooleanRule).get()
                        is GameRules.IntRule -> value = (result as GameRules.IntRule).get()
                        else -> throw RuntimeException("Unknown game rule value type: $result")
                    }
                    translationKey = key.translationKey
                }
            }
        })
        if (value == null) throw IllegalArgumentException("Gamerule $gameruleName not found")
        return Pair(value, translationKey!!)
    }

    /**
     * 回复玩家客户端发送的请求游戏规则的数据包。
     *
     * 接收数据包：
     * * [cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload]
     *
     * 回复数据包：
     * * [ResponseGameruleBooleanS2CPayload]
     * * [ResponseGameruleIntS2CPayload]
     */
    fun respond(player: ServerPlayerEntity, gameruleName: String) {
        if (gameruleName == "ALL") {
            gamerules.accept(object: GameRules.Visitor {
                override fun <T : GameRules.Rule<T?>> visit(key: GameRules.Key<T?>, type: GameRules.Type<T?>) {
                    val (value, key) = getValue(key.toString())
                    when (value) {
                        is Boolean -> {
                            ServerPlayNetworking.send(
                                player, ResponseGameruleBooleanS2CPayload(key, value, key)
                            )
                            logger.debug("Sent ResponseGameruleBooleanS2CPayload: ${key}=${value}")
                        }
                        is Int -> {
                            ServerPlayNetworking.send(
                                player, ResponseGameruleIntS2CPayload(key, value, key)
                            )
                            logger.debug("Sent ResponseGameruleIntS2CPayload: ${key}=${value}")
                        }
                    }
                }
            })
        }
        else {
            val (value, key) = getValue(gameruleName)
            when (value) {
                is Boolean -> ServerPlayNetworking.send(
                    player, ResponseGameruleBooleanS2CPayload(gameruleName, value, key)
                )
                is Int -> ServerPlayNetworking.send(
                    player, ResponseGameruleIntS2CPayload(gameruleName, value, key)
                )
            }
        }
    }

    object Builder {
        var instance: GameRulesManager? = null
        fun build(gamerules: GameRules): GameRulesManager {
            if (instance != null) return instance!!
            instance = GameRulesManager(gamerules)
            return instance!!
        }

        fun delete() {
            instance = null
        }
    }
}