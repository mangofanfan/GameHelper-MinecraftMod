package cn.mangofanfan.gamehelper.client.screen.libgui

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import io.github.cottonmc.cotton.gui.widget.WButton
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.MutableText
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class FButton(translatable: MutableText) : WButton(translatable) {
    var tooltipText: Text? = null

    fun addTooltip(text: Text) {
        tooltipText = text
    }

    override fun addTooltip(tooltip: TooltipBuilder?) {
        if (tooltipText == null) return
        tooltip?.add(tooltipText)
    }
}