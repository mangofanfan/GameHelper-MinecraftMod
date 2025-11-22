package cn.mangofanfan.gamehelper.client

import cn.mangofanfan.gamehelper.client.handler.PlayerDeathHandler
import cn.mangofanfan.gamehelper.client.screen.config.ClientStatus
import cn.mangofanfan.gamehelper.client.screen.ingame.HelperDescription
import cn.mangofanfan.gamehelper.client.screen.ingame.libgui.InGameScreen
import cn.mangofanfan.gamehelper.packet.PlayerDeathS2CPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameHelperClient : ClientModInitializer {
    val logger: Logger = LoggerFactory.getLogger("GamehelperClient")

    /**
     * 按键绑定，默认绑定为键盘`G`。
     */
    private val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.gamehelper.open_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KeyBinding.Category.GAMEPLAY
        )
    )

    override fun onInitializeClient() {
        // 检查是否加载 Cloth Config，若没有则不能打开配置屏幕，并输出警告。
        ClientStatus.Status.isClothConfigLoaded = FabricLoader.getInstance().isModLoaded("cloth-config")
        if (!ClientStatus.Status.isClothConfigLoaded)
            logger.error("Cloth Config is not loaded, please install Cloth Config to use this mod.")

        // 启用按键绑定
        // 只有在游戏中才能触发，此屏幕也只有在游戏中触发才有意义
        ClientTickEvents.END_CLIENT_TICK.register {
            while (keyBinding.wasPressed()) {
                it.setScreen(InGameScreen(HelperDescription(it.currentScreen), it.currentScreen))
            }
        }

        // 注册接收自定义数据包
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            ClientPlayNetworking.registerReceiver(PlayerDeathS2CPayload.Companion.id) { payload, context ->
                logger.info("Received PlayerDeathS2CPayload: ${payload.world}(${payload.pos.x}, ${payload.pos.y}, ${payload.pos.z})")
                context.player().sendMessage(
                    Text.translatable("gamehelper.message.death_position_text", payload.world, payload.pos.x, payload.pos.y, payload.pos.z)
                        .withColor(Colors.YELLOW),
                    false
                )
                context.player().sendMessage(
                    Text.translatable("gamehelper.message.death_position_tp")
                        .styled { style -> style
                            .withBold(true)
                            .withColor(Colors.GREEN)
                            .withUnderline(true)
                            .withClickEvent(ClickEvent.RunCommand("/execute in ${payload.world} run tp ${payload.pos.x} ${payload.pos.y} ${payload.pos.z}"))
                            .withHoverEvent(HoverEvent.ShowText(Text.translatable("gamehelper.screen.death_position.tp.description")))
                        },
                    false
                )
                PlayerDeathHandler.Companion.instance!!.addDeathPos(payload.pos, payload.world)
            }
        }
        logger.info("GamehelperClient init")
    }
}
