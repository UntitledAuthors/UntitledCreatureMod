package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class BlopoleEntityRenderer extends GeoMobRenderer<BlopoleEntity> {
    public BlopoleEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BlopoleEntityModel());
        this.shadowSize = 0.5F;
    }
}
