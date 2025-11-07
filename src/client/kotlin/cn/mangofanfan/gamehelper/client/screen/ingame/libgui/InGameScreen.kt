package cn.mangofanfan.gamehelper.client.screen.ingame.libgui

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
class InGameScreen(description: GuiDescription, val parent: Screen?) : CottonClientScreen(description) {

    override fun close() {
        this.client!!.setScreen(parent)
    }
}