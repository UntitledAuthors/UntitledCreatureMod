package com.untitledauthors.untitledcreaturemod.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class DebugScreen extends Screen {
    private static final int WIDTH = 180;
    private static final int HEIGHT = 150;

    protected DebugScreen() {
        super(new StringTextComponent("Debug"));
    }

    @Override
    protected void init() {
        int x = (this.width - WIDTH) / 2;
        int y = (this.height - HEIGHT) / 2;

        addButton(new Button(x + 10, y + 10, 160, 20, new StringTextComponent("720P"), button -> resize_window(1280, 720)));
        addButton(new Button(x + 10, y + 40, 160, 20, new StringTextComponent("1080P"), button -> resize_window(1920, 1080)));

        addButton(new Button(x + 10, y + 70, 160, 20, new StringTextComponent("ModeToggle"), button -> toggleGameMode()));
        addButton(new Button(x + 10, y + 100, 160, 20, new StringTextComponent("Heal"), button -> heal()));

        addButton(new Button(x + 10, y + 130, 160, 20, new StringTextComponent("Kill NonPlayers"), button -> cmd("/kill @e[type=!minecraft:player]")));

    }

    private void heal() {
        if (minecraft == null) {
            return;
        }
        ClientPlayerEntity player = minecraft.player;
        if (player == null) {
            return;
        }

        String name = player.getName().getString();
        player.sendChatMessage(String.format("/effect give %s minecraft:saturation 10 10 true", name));
    }

    public static void resize_window(int width, int height) {
        MainWindow window = Minecraft.getInstance().getMainWindow();
        GLFW.glfwSetWindowSize(window.getHandle(), width, height);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            this.closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (this.width - WIDTH) / 2;
        int y = (this.height - WIDTH) / 2;

        minecraft.fontRenderer.drawStringWithShadow(matrixStack, this.getTitle().getString(), x + 10, y, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public static void open() {
        Minecraft.getInstance().displayGuiScreen(new DebugScreen());
    }

    public void toggleGameMode() {
        assert minecraft != null;
        assert minecraft.player != null;
        if (minecraft.player.isCreative()) {
            minecraft.player.sendChatMessage("/gamemode survival");
        } else {
            minecraft.player.sendChatMessage("/gamemode creative");
        }
    }

    public void cmd(String cmd) {
        assert minecraft != null;
        assert minecraft.player != null;
        minecraft.player.sendChatMessage(cmd);
    }

}
