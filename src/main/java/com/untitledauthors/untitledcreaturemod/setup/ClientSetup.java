package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntityRenderer;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntityRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


public class ClientSetup {
    public static void setupCreatureRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Registration.TOAD.get(), ToadEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.ROCK_ANTELOPE.get(), RockAntelopeEntityRenderer::new);
    }
}
