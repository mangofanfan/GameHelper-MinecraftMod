package cn.mangofanfan.gamehelper

import cn.mangofanfan.gamehelper.config.ConfigManager
import cn.mangofanfan.gamehelper.packet.PlayerDeathS2CPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameHelper : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("GameHelper")

    override fun onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register {
            entity, damageSource ->
            if (entity.isPlayer) {
                handlePlayerDeath(entity as ServerPlayerEntity, damageSource)
            }
        }
        PayloadTypeRegistry.playS2C().register(
            PlayerDeathS2CPayload.Companion.id,
            PlayerDeathS2CPayload.Companion.CODEC
        )
    }

    private fun handlePlayerDeath(entity: ServerPlayerEntity, damageSource: DamageSource) {
        logger.info("Player ${entity.name} died at ${entity.blockPos} because of ${damageSource.name}.")
        if (ConfigManager.getInstance().config.recordDeathPosition) {
            ServerPlayNetworking.send(entity, PlayerDeathS2CPayload(
                entity.blockPos,
                entity.entityWorld.registryKey.value.toString()
            ))
        }
        else {
            logger.info("According to config, death position is not recorded.")
        }
    }
}
