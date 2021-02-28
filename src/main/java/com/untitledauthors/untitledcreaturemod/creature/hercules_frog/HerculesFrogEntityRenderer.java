package com.untitledauthors.untitledcreaturemod.creature.hercules_frog;

import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HerculesFrogEntityRenderer extends GeoMobRenderer<HerculesFrogEntity> {
    public HerculesFrogEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HerculesFrogEntityModel());
        this.shadowSize = 1.25F;
    }
}
