package cn.mangofanfan.gamehelper.data

import cn.mangofanfan.gamehelper.packet.PlayerDeathSyncS2CPayload
import cn.mangofanfan.tools.file.FDirectory
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.WorldSavePath
import net.minecraft.util.math.BlockPos
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DeathPositionDataManager(server: MinecraftServer) {
    /**
     * 单例模式获取`DeathPositionDataManager`实例。
     */
    object Companion {
        var instance: DeathPositionDataManager? = null

        /**
         * 使用`create`方法创建`DeathPositionDataManager`实例，并传入 `MinecraftServer` 实例。
         */
        fun create(server: MinecraftServer): DeathPositionDataManager {
            if (instance == null) {
                instance = DeathPositionDataManager(server)
            }
            return instance!!
        }

        fun stop() {
            instance = null
            // TODO 关闭前保存数据
        }
    }

    /**
     * 服务器世界存档目录。
     *
     * 在单人游戏中，是`/saves/<world_name>/.`。
     */
    var saveDirectory: FDirectory? = null

    /**
     * fan_mod_data下的death目录。
     *
     * 在单人游戏中，是`/saves/<world_name>/fan_mod_data/death/`。
     */
    var deathPositionDirectory: FDirectory? = null

    val logger: Logger = LoggerFactory.getLogger("DeathPositionDataManager")

    /**
     * 字典，存储玩家UUID与对应的`DeathPositionData`。
     */
    var dataMap = mutableMapOf<String, DeathPositionData>()

    init {
        // 获取目录，如果不存在则创建
        saveDirectory = FDirectory.of(server.getSavePath(WorldSavePath.ROOT).toString())
        deathPositionDirectory = saveDirectory!!
            .createSubDirectory("fan_mod_data", true)
            .createSubDirectory("death", true)
    }

    /**
     * 在玩家加入游戏时，创建或读取该玩家的死亡信息。
     */
    fun readOrCreatePlayerDeathPositionData(player: ServerPlayerEntity) {
        val file = deathPositionDirectory!!.getSubFile(player.uuid.toString() + ".json")
        var playerData: DeathPositionData?
        if (file.isExited) {
            playerData = file.readJson(DeathPositionData::class.java)
        }
        else {
            playerData = DeathPositionData()
            playerData.uuid = player.uuid.toString()
            file.create()
            file.writeJson(playerData)
        }
        // 添加到字典中
        dataMap[player.uuid.toString()] = playerData
        logger.debug("readOrCreatePlayerDeathPositionData: {} ({})", player.name.toString(), player.uuid.toString())
    }

    /**
     * 在玩家死亡时，将新的死亡信息添加到文件中
     */
    fun addDeathPosition(player: ServerPlayerEntity, pos: BlockPos, world: String) {
        val playerData = dataMap[player.uuid.toString()]!!
        playerData.addDeathPosition(pos, world)
        val file = deathPositionDirectory!!.getSubFile(player.uuid.toString() + ".json")
        if (file.isExited) {
            file.writeJson(playerData)
        }
        else {
            throw RuntimeException("Player deathPositionData json file [ $file ] not found.")
        }
        logger.debug("addDeathPosition: {} ({})", player.name.toString(), player.uuid.toString())
    }

    /**
     * 向玩家同步所有死亡信息
     */
    fun sendSyncPacket(player: ServerPlayerEntity) {
        for (dp in dataMap[player.uuid.toString()]!!.positionList) {
            ServerPlayNetworking.send(player,
                PlayerDeathSyncS2CPayload(dp.x!!, dp.y!!, dp.z!!, dp.world!!)
            )
        }
    }
}