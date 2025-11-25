package cn.mangofanfan.gamehelper

import cn.mangofanfan.gamehelper.config.ConfigManager
import cn.mangofanfan.gamehelper.data.DeathPositionDataManager
import cn.mangofanfan.gamehelper.gamerules.GameRulesManager
import cn.mangofanfan.gamehelper.packet.PlayerDeathS2CPayload
import cn.mangofanfan.gamehelper.packet.PlayerDeathSyncS2CPayload
import cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload
import cn.mangofanfan.gamehelper.packet.RequestResyncDeathPositionsC2SPayload
import cn.mangofanfan.gamehelper.packet.ResponseGameruleBooleanS2CPayload
import cn.mangofanfan.gamehelper.packet.ResponseGameruleIntS2CPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameHelper : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("GameHelper")
    private val configManager = ConfigManager.getInstance()

    override fun onInitialize() {
        // 服务器启动时注册服务端数据存储，服务器关闭时删除
        ServerLifecycleEvents.SERVER_STARTED.register {
            server ->
            DeathPositionDataManager.Companion.create(server)
            GameRulesManager.Builder.build(server.gameRules)
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            DeathPositionDataManager.Companion.stop()
            GameRulesManager.Builder.delete()
        }

        // 注册玩家加入时读取玩家死亡信息
        ServerPlayerEvents.JOIN.register {
            player ->
            DeathPositionDataManager.Companion.instance!!.readOrCreatePlayerDeathPositionData(player)
        }

        // 注册玩家死亡事件监听
        ServerLivingEntityEvents.AFTER_DEATH.register {
            entity, damageSource ->
            if (entity.isPlayer) {
                handlePlayerDeath(entity as ServerPlayerEntity, damageSource)
            }
        }

        // 注册自定义数据包
        PayloadTypeRegistry.playS2C().register(
            PlayerDeathS2CPayload.Companion.id,
            PlayerDeathS2CPayload.Companion.CODEC
        )
        PayloadTypeRegistry.playS2C().register(
            PlayerDeathSyncS2CPayload.Companion.id,
            PlayerDeathSyncS2CPayload.Companion.CODEC
        )
        PayloadTypeRegistry.playC2S().register(
            RequestResyncDeathPositionsC2SPayload.ID,
            RequestResyncDeathPositionsC2SPayload.CODEC
        )
        PayloadTypeRegistry.playC2S().register(
            RequestGameruleC2SPayload.Companion.id,
            RequestGameruleC2SPayload.Companion.CODEC
        )
        PayloadTypeRegistry.playS2C().register(
            ResponseGameruleBooleanS2CPayload.Companion.id,
            ResponseGameruleBooleanS2CPayload.Companion.CODEC
        )
        PayloadTypeRegistry.playS2C().register(
            ResponseGameruleIntS2CPayload.Companion.id,
            ResponseGameruleIntS2CPayload.Companion.CODEC
        )

        // 注册接收到客户端数据包时事件
        ServerPlayNetworking.registerGlobalReceiver(RequestResyncDeathPositionsC2SPayload.ID) {
            _, context ->
            if (configManager.config.recordDeathPosition) {
                DeathPositionDataManager.Companion.instance!!.sendSyncPacket(context.player())
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(RequestGameruleC2SPayload.Companion.id) {
            payload, context ->
            if (configManager.config.enableGameRulesManager) {
                // 如果服务器设置了 disableGameRulesForAnyone，且该玩家权限不足，则忽略
                if (configManager.config.disableGameRulesForAnyone && context.player().permissionLevel < 2) {
                    context.player().sendMessage(Text.translatable("gamehelper.message.permission_denied").withColor(
                        Colors.YELLOW))
                    return@registerGlobalReceiver
                }
                val manager = GameRulesManager.Builder.instance!!
                manager.respond(context.player(), payload.gameruleName!!)
                logger.info("${context.player().name} requested gamerule ${payload.gameruleName}.")
            }
        }
    }

    private fun handlePlayerDeath(entity: ServerPlayerEntity, damageSource: DamageSource) {
        logger.info("Player ${entity.name} died at ${entity.blockPos} because of ${damageSource.name}.")
        if (configManager.config.recordDeathPosition) {
            ServerPlayNetworking.send(entity, PlayerDeathS2CPayload(
                entity.blockPos,
                entity.entityWorld.registryKey.value.toString()
            ))
            DeathPositionDataManager.Companion.instance!!.addDeathPosition(entity, entity.blockPos,  entity.entityWorld.registryKey.value.toString())
        }
        else {
            logger.info("According to config, death position is not recorded.")
        }
    }
}
