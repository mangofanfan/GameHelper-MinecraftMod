package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 从客户端向服务端发送的数据包，请求游戏规则`gamerule`的值。布尔值类型
 *
 * **在单人游戏中不应该处理此数据包。**
 */
class ResponseGameruleBooleanS2CPayload(val gameruleName: String, val value: Boolean, val translationKey: String) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return Companion.id
    }

    object Companion {
        val RESPONSE_GAMERULE_BOOLEAN_ID = Identifier.of("gamehelper", "response_gamerule_boolean")!!
        val id = CustomPayload.id<ResponseGameruleBooleanS2CPayload>(RESPONSE_GAMERULE_BOOLEAN_ID.toTranslationKey())!!

        val CODEC: PacketCodec<RegistryByteBuf, ResponseGameruleBooleanS2CPayload> = PacketCodec.tuple(
            PacketCodecs.STRING,
            ResponseGameruleBooleanS2CPayload::gameruleName,
            PacketCodecs.BOOLEAN,
            ResponseGameruleBooleanS2CPayload::value,
            PacketCodecs.STRING,
            ResponseGameruleBooleanS2CPayload::translationKey
        ) { gameruleName, value, translationKey -> ResponseGameruleBooleanS2CPayload(gameruleName, value, translationKey) }
    }
}