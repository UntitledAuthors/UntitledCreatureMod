package net.untitledcreaturemod.creature.pelican;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class PelicanEntityRenderer extends GeoEntityRenderer<PelicanEntity> {
    public PelicanEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new PelicanModel());
        this.shadowRadius = 0.3f;
    }
}
