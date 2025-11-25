package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 空负载数据包，用来于客户端向服务端请求重新发送完整的死亡信息。将在客户端的`PlayerDeathHandler`中被发送。
 */
object RequestResyncDeathPositionsC2SPayload : CustomPayload {
    val REQUEST_RESYNC_DEATH_POSITIONS_ID = Identifier.of("gamehelper", "request_resync")!!

    val ID = CustomPayload.id<RequestResyncDeathPositionsC2SPayload>(REQUEST_RESYNC_DEATH_POSITIONS_ID.toTranslationKey())!!

    val CODEC: PacketCodec<RegistryByteBuf, RequestResyncDeathPositionsC2SPayload> = PacketCodec.unit(this)

    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return ID
    }
}