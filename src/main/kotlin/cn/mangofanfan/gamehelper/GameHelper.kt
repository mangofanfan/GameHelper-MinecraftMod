package cn.mangofanfan.gamehelper

import cn.mangofanfan.gamehelper.config.ConfigManager
import cn.mangofanfan.gamehelper.data.DeathPositionDataManager
import cn.mangofanfan.gamehelper.gamerules.GameRulesManager
import cn.mangofanfan.gamehelper.packet.PlayerDeathS2CPayload
import cn.mangofanfan.gamehelper.packet.PlayerDeathSyncS2CPayload
import cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload
import cn.mangofanfan.gamehelper.packet.RequestResyncC2SPayload
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameHelper : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("GameHelper")

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

        // 注册玩家登录时事件
        ServerPlayerEvents.JOIN.register {
            player ->
            val manager = DeathPositionDataManager.Companion.instance!!
            // 读取该玩家在本服务器/存档中的死亡记录
            manager.readOrCreatePlayerDeathPositionData(player)
            // 然后把它们发送给玩家
            manager.sendSyncPacket(player)
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
            RequestResyncC2SPayload.ID,
            RequestResyncC2SPayload.CODEC
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
        ServerPlayNetworking.registerGlobalReceiver(RequestResyncC2SPayload.ID) {
            _, context ->
            DeathPositionDataManager.Companion.instance!!.sendSyncPacket(context.player())
        }
        ServerPlayNetworking.registerGlobalReceiver(RequestGameruleC2SPayload.Companion.id) {
            payload, context ->
            val manager = GameRulesManager.Builder.instance!!
            manager.respond(context.player(), payload.gameruleName!!)
            logger.info("${context.player().name} requested gamerule ${payload.gameruleName}.")
        }
    }

    private fun handlePlayerDeath(entity: ServerPlayerEntity, damageSource: DamageSource) {
        logger.info("Player ${entity.name} died at ${entity.blockPos} because of ${damageSource.name}.")
        if (ConfigManager.getInstance().config.recordDeathPosition) {
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
