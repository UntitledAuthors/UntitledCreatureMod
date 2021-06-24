package net.untitledcreaturemod.forge.renderers;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.untitledcreaturemod.creature.toad.ToadEntity;
import net.untitledcreaturemod.creature.toad.ToadModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ToadEntityRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new ToadModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public Identifier getTexture(ToadEntity arg) {
        return getTexture(arg);
    }
}
