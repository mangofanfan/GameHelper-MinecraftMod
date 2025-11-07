package cn.mangofanfan.gamehelper.client.screen.config

import com.google.gson.Gson
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Environment(EnvType.CLIENT)
class ConfigManager {
    val logger: Logger = LoggerFactory.getLogger("GameHelper ConfigManager")
    val CONFIG_PATH: Path = FabricLoader.getInstance().configDir.resolve("gamehelper.json")

    class Config {
        /**
         * 在G键打开的 Helper GUI 中，游戏规则屏幕内直接显示游戏规则的翻译键文本，而不是规则名。
         *
         * 默认为false。GUI中直接显示规则名，翻译键文本显示在工具提示中。
         *
         * 为true时，GUI中直接显示翻译键文本，规则名将显示在工具提示中。
         */
        var showGameruleTranslationInGUI = false

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
            Files.newBufferedReader(CONFIG_PATH).use { reader ->
                config = Gson().fromJson(reader, Config::class.java)
            }
        } catch (_: IOException) {
            config = Config() // 创建默认配置
            saveConfig()
        } catch (_: NoSuchFieldError) {
            config = Config()
            saveConfig()
        }
    }

    fun saveConfig() {
        try {
            Files.newBufferedWriter(CONFIG_PATH).use { writer ->
                Gson().toJson(config, writer)
            }
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