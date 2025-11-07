package cn.mangofanfan.gamehelper.client.screen.ingame.libgui

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import io.github.cottonmc.cotton.gui.widget.WLabel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class FLabel(translate: Text): WLabel(translate) {
    var tooltipText: Text? = null

    fun addTooltip(text: Text) {
        tooltipText = text
    }

    override fun addTooltip(tooltip: TooltipBuilder?) {
        if (tooltipText == null) return
        tooltip?.add(tooltipText)
    }
}