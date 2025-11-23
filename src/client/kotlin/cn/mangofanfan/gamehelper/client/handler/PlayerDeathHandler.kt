package cn.mangofanfan.gamehelper.client.handler

import cn.mangofanfan.gamehelper.client.handler.info.DeathPosition
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.math.BlockPos

/**
 * `PlayerDeathHandler`是仅在客户端运行的玩家死亡信息处理器。
 *
 * 该类为全局单例模式，每当玩家从世界或服务器退出时都会清空存储的死亡信息。
 */
@Environment(EnvType.CLIENT)
class PlayerDeathHandler {
    /**
     * 玩家的死亡坐标列表
     */
    var deathPosList = mutableListOf<DeathPosition>()
        get() {
            for (deathPos in field) {
                deathPos.id = field.indexOf(deathPos) + 1
            }
            field.sortBy { it.id }
            DeathPosition.Builder.id = field.size - 1
            return field
        }

    fun addDeathPos(pos: BlockPos, world: String) {
        deathPosList.add(DeathPosition.Builder.build(pos, world))
    }

    fun addDeathPos(x: Int, y: Int, z: Int, world: String) {
        addDeathPos(BlockPos(x, y, z), world)
    }

    fun clearDeathPos() {
        deathPosList.clear()
    }

    fun removeDeathPos(deathPosition: DeathPosition) {
        deathPosList.removeIf { it.id == deathPosition.id }
    }

    object Companion {
        var instance: PlayerDeathHandler? = null
            get() {
                if (field == null) {
                    field = PlayerDeathHandler()
                }
                return field
            }
    }
}
