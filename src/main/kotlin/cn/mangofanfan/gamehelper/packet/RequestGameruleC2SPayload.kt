package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 从客户端向服务端发送的数据包，请求游戏规则`gamerule`的值。根据规则类型，可能返回布尔值或整型结果。
 *
 * **在单人游戏中不应该处理此数据包。**
 */
class RequestGameruleC2SPayload(var gameruleName: String?) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return Companion.id
    }

    init {
        if (gameruleName == null) gameruleName = "ALL"
    }

    object Companion {
        val REQUEST_GAMERULE_ID = Identifier.of("gamehelper", "request_gamerule")!!
        val id = CustomPayload.id<RequestGameruleC2SPayload>(REQUEST_GAMERULE_ID.toTranslationKey())!!

        val CODEC: PacketCodec<RegistryByteBuf, RequestGameruleC2SPayload> = PacketCodec.tuple(
            PacketCodecs.STRING,
            RequestGameruleC2SPayload::gameruleName
        ) { gameruleName -> RequestGameruleC2SPayload(gameruleName) }
    }
}