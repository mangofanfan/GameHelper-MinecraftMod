package cn.mangofanfan.gamehelper.config

import cn.mangofanfan.tools.file.FFile
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path

class ConfigManager {
    private val logger: Logger = LoggerFactory.getLogger("GameHelper ConfigManager")
    private val configPath: Path = FabricLoader.getInstance().configDir.resolve("gamehelper.json")
    private val configFile = FFile.of(configPath)

    class Config {
        /**
         * 在G键打开的 Helper GUI 中，游戏规则屏幕内直接显示游戏规则的翻译键文本，而不是规则名。
         *
         * 默认为false。GUI中直接显示规则名，翻译键文本显示在工具提示中。
         *
         * 为true时，GUI中直接显示翻译键文本，规则名将显示在工具提示中。
         */
        var showGameruleTranslationInGUI = false

        /**
         * 在游戏中显示更多信息，主要是本模组在聊天栏中输出的记录性文本。
         *
         * 默认为true，输出。
         */
        var showMoreInfoInGame = true

        /**
         * 记录死亡坐标。默认为开。
         *
         * 在客户端的单人游戏中，这简单地决定了模组是否记录玩家的死亡地点；
         *
         * 在多人游戏中，需要服务器或主机打开此配置，服务端才会向各玩家发送死亡坐标；但无论客户端是否打开此配置，都可以显示服务端发送的死亡坐标。
         */
        var recordDeathPosition = true

        /**
         * 启用服务端游戏规则管理。默认为true。
         *
         * 如果为开，服务端将回复每个玩家客户端发送的游戏规则请求（[cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload]）。
         * 这将允许任何加入的玩家查看服务器的游戏规则。
         *
         * 如果为关，则服务器不会回应任何游戏规则请求，除了单人游戏或使用单人游戏进行LAN联机时的你自己。（因为这时不使用网络数据包获取游戏规则）
         */
        var enableGameRulesManager = true

        /**
         * 不允许任何人获取游戏规则，只允许管理员获取游戏规则，默认为true。
         *
         * 如果为关，服务端将响应每个玩家客户端发送的游戏规则请求（[cn.mangofanfan.gamehelper.packet.RequestGameruleC2SPayload]）。
         *
         * 如果为开，服务端只会响应拥有权限的玩家发送的游戏规则请求，例如服务器的 op。
         */
        var disableGameRulesForAnyone = true
    }

    var config: Config = Config()

    /**
     * 初始化配置文件，然后可以使用`ConfigManager.config`获取配置项。
     *
     * 如果配置文件不存在（即首次启动时）将自动创建，否则会读取已存在的配置。
     *
     * @return null.
     */
    fun loadConfig() {
        try {
            config = configFile.readJson(Config::class.java)
        } catch (_: Exception) {
            config = Config() // 创建默认配置
            saveConfig()
        }
    }

    fun saveConfig() {
        try {
            configFile.writeJson(config)
        } catch (e: IOException) {
            logger.error(
                "Exception occurred when saving config !{}",
                e.toString()
            )
        }
    }

    /**
     * 单例模式获取`ConfigManager`实例。
     */
    companion object {
        @JvmStatic
        private var instance: ConfigManager? = null
        fun getInstance(): ConfigManager {
            if (instance == null) {
                instance = ConfigManager()
                instance!!.loadConfig()
            }
            return instance!!
        }
    }
}