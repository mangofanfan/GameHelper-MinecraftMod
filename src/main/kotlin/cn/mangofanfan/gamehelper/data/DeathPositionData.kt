package cn.mangofanfan.gamehelper.data

import net.minecraft.util.math.BlockPos

/**
 * 玩家死亡坐标数据。
 *
 * @property uuid 玩家UUID。
 * @property positionList 玩家死亡坐标列表。
 */
class DeathPositionData {

    class DeathPosition {
        var x: Int? = null
        var y: Int? = null
        var z: Int? = null
        var world: String? = null
    }

    fun addDeathPosition(blockPos: BlockPos, world: String) {
        val dp = DeathPosition()
        dp.x = blockPos.x
        dp.y = blockPos.y
        dp.z = blockPos.z
        dp.world = world
        positionList.add(dp)
    }

    var uuid: String = ""
    var positionList: ArrayList<DeathPosition> = ArrayList()
}
