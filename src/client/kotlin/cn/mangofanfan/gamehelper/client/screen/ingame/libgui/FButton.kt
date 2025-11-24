package cn.mangofanfan.gamehelper.client.screen.ingame.libgui

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import io.github.cottonmc.cotton.gui.widget.WButton
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.MutableText
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class FButton(translatable: MutableText) : WButton(translatable) {
    var tooltipTexts: ArrayList<Text> = ArrayList()

    fun addTooltip(text: Text) {
        tooltipTexts.add(text)
    }

    override fun addTooltip(tooltip: TooltipBuilder?) {
        for (tooltipText in tooltipTexts) {
            tooltip?.add(tooltipText)
        }
    }
}