package cn.mangofanfan.gamehelper.client

import cn.mangofanfan.gamehelper.client.screen.config.HelperConfigScreenBuilder
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen

class GameHelperModMenuApiImpl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? -> HelperConfigScreenBuilder(parent!!).build() }
    }
}
