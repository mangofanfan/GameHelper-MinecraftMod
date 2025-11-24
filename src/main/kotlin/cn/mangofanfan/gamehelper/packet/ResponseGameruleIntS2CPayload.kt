package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 从客户端向服务端发送的数据包，请求游戏规则`gamerule`的值。整型类型
 *
 * **在单人游戏中不应该处理此数据包。**
 */
class ResponseGameruleIntS2CPayload(val gameruleName: String, val value: Int, val translationKey: String) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return Companion.id
    }

    object Companion {
        val RESPONSE_GAMERULE_INT_ID = Identifier.of("gamehelper", "response_gamerule_int")!!
        val id = CustomPayload.id<ResponseGameruleIntS2CPayload>(RESPONSE_GAMERULE_INT_ID.toTranslationKey())!!

        val CODEC: PacketCodec<RegistryByteBuf, ResponseGameruleIntS2CPayload> = PacketCodec.tuple(
            PacketCodecs.STRING,
            ResponseGameruleIntS2CPayload::gameruleName,
            PacketCodecs.INTEGER,
            ResponseGameruleIntS2CPayload::value,
            PacketCodecs.STRING,
            ResponseGameruleIntS2CPayload::translationKey
        ) { gameruleName, value, translationKey -> ResponseGameruleIntS2CPayload(gameruleName, value, translationKey) }
    }
}