package cn.mangofanfan.gamehelper.packet

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * 从服务端向客户端发送的数据包，回复客户端对于`gameruleName`游戏规则的请求。
 *
 * **在单人游戏中不应该处理此数据包。**
 *
 * 由于Minecraft的游戏规则有两种值的类型，因此回复游戏规则的数据包也有两种：
 * * [cn.mangofanfan.gamehelper.packet.ResponseGameruleBooleanS2CPayload] 用于 [Boolean]
 * * [cn.mangofanfan.gamehelper.packet.ResponseGameruleIntS2CPayload] 用于 [Int]
 *
 * @param gameruleName 回复的游戏规则的名称，[String]
 * @param value 游戏规则的值，本数据包需要为[Int]
 * @param translationKey 游戏规则的翻译键，用于在客户端中显示，[String]
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