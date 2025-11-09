package cn.mangofanfan.gamehelper.client.screen.ingame.libgui

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

/**
 * 这是Libgui实现的 Helper GUI 的包装器。
 *
 * libgui被嵌入本模组中，因此无需检查libgui是否存在，此 Helper GUI 一定可以打开。
 */
@Environment(EnvType.CLIENT)
class InGameScreen(description: GuiDescription, val parent: Screen?) : CottonClientScreen(description) {
    override fun close() {
        this.client!!.setScreen(parent)
    }
}