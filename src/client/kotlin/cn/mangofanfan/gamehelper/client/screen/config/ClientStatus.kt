package cn.mangofanfan.gamehelper.client.screen.config

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * 本模组的内部状态。
 */
@Environment(EnvType.CLIENT)
class ClientStatus {
    object Status {
        /**
         * Cloth config是否已加载
         *
         * 如果没有加载，则不应该允许打开配置屏幕。
         */
        var isClothConfigLoaded = false
    }
}