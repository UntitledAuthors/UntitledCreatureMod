package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


public class ClientSetup {
    public static void setupEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Registration.TOAD.get(), ToadEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROCK_ANTELOPE.get(), RockAntelopeEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.POISONOUS_SECRETIONS_PROJECTILE.get(),
                erm -> new SpriteRenderer<>(erm, Minecraft.getInstance().getItemRenderer()));
    }
}
