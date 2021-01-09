package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RockAntelopeEntityRenderer extends GeoEntityRenderer<RockAntelopeEntity> {
    public RockAntelopeEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new RockAntelopeEntityModel());
        this.shadowSize = 0.8F;
    }
}
