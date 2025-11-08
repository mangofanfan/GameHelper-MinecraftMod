package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class PlayerDeathS2CPayload(val pos: BlockPos, val world: String) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?>? {
        return Companion.id
    }

    object Companion {
        val PLAYER_DEATH_ID = Identifier.of("gamehelper", "player_death")
        val id = CustomPayload.id<PlayerDeathS2CPayload>(PLAYER_DEATH_ID.toTranslationKey())
        val CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC,
            PlayerDeathS2CPayload::pos,
            PacketCodecs.STRING,
            PlayerDeathS2CPayload::world
        ) { pos, world -> PlayerDeathS2CPayload(pos, world) }
    }
}