package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class ToadEntityRenderer extends GeoMobRenderer<ToadEntity> {
    public ToadEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ToadEntityModel());
        this.shadowSize = 0.4F;
    }
}
