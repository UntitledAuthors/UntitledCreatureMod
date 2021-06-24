package net.untitledcreaturemod.creature.toad;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class ToadEntityRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new ToadModel());
        this.shadowRadius = 0.3f;
    }
}
