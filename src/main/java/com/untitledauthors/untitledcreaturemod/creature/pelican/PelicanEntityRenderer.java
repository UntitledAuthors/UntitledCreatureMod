package com.untitledauthors.untitledcreaturemod.creature.pelican;

import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class PelicanEntityRenderer extends GeoMobRenderer<PelicanEntity> {
    public PelicanEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new PelicanEntityModel());
        this.shadowSize = 0.5F;
    }
}
