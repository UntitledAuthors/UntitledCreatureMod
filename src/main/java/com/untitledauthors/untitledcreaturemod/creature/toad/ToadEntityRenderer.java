package com.untitledauthors.untitledcreaturemod.creature.toad;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ToadEntityRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ToadEntityModel());
        this.shadowSize = 0.4F;
    }
}
