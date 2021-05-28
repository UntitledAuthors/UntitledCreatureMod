package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.blopole.BlopoleEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.hercules_frog.HerculesFrogEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.pelican.PelicanEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


public class ClientSetup {
    public static void setupEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Registration.TOAD.get(), ToadEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROCK_ANTELOPE.get(), RockAntelopeEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.BLOPOLE.get(), BlopoleEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.HERCULES_FROG.get(), HerculesFrogEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.PELICAN.get(), PelicanEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.POISONOUS_SECRETIONS_PROJECTILE.get(),
                erm -> new SpriteRenderer<>(erm, Minecraft.getInstance().getItemRenderer()));

    }

    public static void setupBlockRendering() {
        RenderTypeLookup.setRenderLayer(Registration.POISONOUS_SECRETIONS_CARPET.get(), RenderType.getCutout());
    }

    public static void setup() {
        setupEntityRenderers();
        setupBlockRendering();
    }
}
