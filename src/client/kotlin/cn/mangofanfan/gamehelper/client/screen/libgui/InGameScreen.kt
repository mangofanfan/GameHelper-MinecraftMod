package cn.mangofanfan.gamehelper.client.screen.libgui

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
class InGameScreen(description: GuiDescription, parent: Screen?) : CottonClientScreen(description) {
    val _parent: Screen? = parent

    override fun close() {
        this.client!!.setScreen(_parent)
    }
}