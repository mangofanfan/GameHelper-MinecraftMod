package cn.mangofanfan.gamehelper.client

import cn.mangofanfan.gamehelper.client.screen.widget.HelperDescription
import cn.mangofanfan.gamehelper.client.screen.libgui.InGameScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GamehelperClient : ClientModInitializer {
    val logger: Logger = LoggerFactory.getLogger("GamehelperClient")

    /**
     * 按键绑定，默认绑定为键盘`G`。
     */
    private val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.gamehelper.open_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KeyBinding.Category.GAMEPLAY
        )
    )

    override fun onInitializeClient() {
        // 启用按键绑定
        // 只有在游戏中才能触发，此屏幕也只有在游戏中触发才有意义
        ClientTickEvents.END_CLIENT_TICK.register {
            while (keyBinding.wasPressed()) {
                it.setScreen(InGameScreen(HelperDescription(it.currentScreen), it.currentScreen))
            }
        }
        logger.info("GamehelperClient init")
    }
}
