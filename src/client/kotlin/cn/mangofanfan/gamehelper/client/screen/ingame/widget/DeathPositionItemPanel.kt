package cn.mangofanfan.gamehelper.client.screen.ingame.widget

import cn.mangofanfan.gamehelper.client.handler.PlayerDeathHandler
import cn.mangofanfan.gamehelper.client.handler.info.DeathPosition
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FButton
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.FLabel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Colors

class DeathPositionItemPanel: WPlainPanel() {
    var nameLabel: FLabel? = null
    var tpButton: FButton? = null
    var delButton: FButton? = null

    var deathPosition: DeathPosition? = null
        set(value) {
            field = value
            nameLabel!!.text = Text.translatable(
                "gamehelper.screen.death_position.name",
                value!!.id, value.pos.x, value.pos.y, value.pos.z)
            nameLabel!!.addTooltip(Text.translatable("gamehelper.screen.death_position.description", value.world))
            tpButton!!.setOnClick {
                MinecraftClient.getInstance().networkHandler!!.sendChatCommand(
                    "execute in ${deathPosition!!.world} at @s run tp ${deathPosition!!.pos.x} ${deathPosition!!.pos.y} ${deathPosition!!.pos.z}"
                )
            }
            delButton!!.setOnClick {
                PlayerDeathHandler.Companion.instance!!.removeDeathPos(value)
                nameLabel!!.text = Text.translatable("gamehelper.screen.death_position.deleted")
            }
            if (value.id % 2 != 0) {
                nameLabel!!.color = Colors.GRAY
            }
        }

    init {
        setSize(18*12, 18)
        nameLabel = FLabel(Text.literal("poq"))
        nameLabel!!.setVerticalAlignment(VerticalAlignment.CENTER)
        nameLabel!!.setHorizontalAlignment(HorizontalAlignment.LEFT)
        tpButton = FButton(Text.translatable("gamehelper.screen.death_position.button"))
        tpButton!!.addTooltip(Text.translatable("gamehelper.screen.death_position.button.description"))
        delButton = FButton(Text.translatable("gamehelper.screen.death_position.del"))
        delButton!!.addTooltip(Text.translatable("gamehelper.screen.death_position.del.description"))

        add(nameLabel, 0, 0, 18*12, 18)
        add(tpButton, 18*7, 0, 18*2, 18)
        add(delButton, 18*9, 0, 18*2, 18)
    }
}