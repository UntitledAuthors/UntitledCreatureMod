package com.untitledauthors.untitledcreaturemod.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class DebugHUD {
    public static boolean enabled = false;
    public static ClientWorld world;
    public static ClientPlayerEntity player;

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        player = event.getPlayer();
        if (player == null) {
            return;
        }
        world = player.connection.getWorld();
    }

    @SubscribeEvent
    public static void onPlayerLogOff(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        player = null;
        world = null;
        enabled = false;
    }

    @SubscribeEvent
    public static void onDraw(RenderGameOverlayEvent.Post event) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MatrixStack ms = event.getMatrixStack();
            if (player != null) {
                mc.fontRenderer.drawStringWithShadow(ms,
                        String.format("Rotation %s", player.getLookVec()), 0, 0, 0xFFFFFF);
                mc.fontRenderer.drawStringWithShadow(ms,
                        String.format("Motion %s", player.getMotion()), 0, 10, 0xFFFFFF);
            }
        }
    }

    // TODO: This should probably be moved
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().currentScreen != null) {
            return;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_M) {
            enabled = !enabled;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_R) {
            DebugScreen.open();
        }
    }
}
