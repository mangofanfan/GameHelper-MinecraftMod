package cn.mangofanfan.gamehelper.client.handler.info

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.math.BlockPos

@Environment(EnvType.CLIENT)
class DeathPosition(val pos: BlockPos, val world: String, var id: Int) {
    /**
     * 构建者模式
     */
    object Builder {
        var id = 0

        fun build(pos: BlockPos, world: String): DeathPosition {
            return DeathPosition(pos, world, ++id)
        }
    }
}