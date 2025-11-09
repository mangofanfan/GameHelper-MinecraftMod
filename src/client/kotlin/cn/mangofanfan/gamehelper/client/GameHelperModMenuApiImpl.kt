package cn.mangofanfan.gamehelper.client

import cn.mangofanfan.gamehelper.client.screen.config.ClientStatus
import cn.mangofanfan.gamehelper.client.screen.config.HelperConfigScreenBuilder
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen

class GameHelperModMenuApiImpl : ModMenuApi {
    /**
     * 获取配置屏幕工厂
     *
     * 确保在未安装 Cloth Config 时，不会因为尝试打开配置屏幕而崩溃。
     */
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? ->
            if (ClientStatus.Status.isClothConfigLoaded) HelperConfigScreenBuilder(parent!!).build() else null }
    }
}
