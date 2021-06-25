package net.untitledcreaturemod.creature.toad;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ToadEntityRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ToadModel());
        this.shadowRadius = 0.3f;
    }
}
