package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 空负载数据包，用来于客户端向服务端请求重新发送完整的死亡信息
 */
object RequestResyncC2SPayload : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return id
    }

    val REQUEST_RESYNC_ID = Identifier.of("gamehelper", "request_resync")!!
    val id = CustomPayload.id<RequestResyncC2SPayload>(REQUEST_RESYNC_ID.toTranslationKey())!!
    val CODEC: PacketCodec<RegistryByteBuf, RequestResyncC2SPayload> = PacketCodec.unit(this)

}