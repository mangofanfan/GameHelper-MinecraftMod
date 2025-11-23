package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

class PlayerDeathSyncS2CPayload(val x: Int, val y: Int, val z: Int, val world: String) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return Companion.id
    }

    object Companion {
        val PLAYER_DEATH_SYNC_ID = Identifier.of("gamehelper", "player_death_sync")!!
        val id = CustomPayload.id<PlayerDeathSyncS2CPayload>(PLAYER_DEATH_SYNC_ID.toTranslationKey())!!
        val CODEC: PacketCodec<RegistryByteBuf, PlayerDeathSyncS2CPayload> = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            PlayerDeathSyncS2CPayload::x,
            PacketCodecs.INTEGER,
            PlayerDeathSyncS2CPayload::y,
            PacketCodecs.INTEGER,
            PlayerDeathSyncS2CPayload::z,
            PacketCodecs.STRING,
            PlayerDeathSyncS2CPayload::world
        ) { x, y, z, world -> PlayerDeathSyncS2CPayload(x, y, z, world) }
    }
}