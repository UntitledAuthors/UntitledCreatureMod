package net.untitledcreaturemod.forge.renderers;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.untitledcreaturemod.creature.pelican.PelicanEntity;
import net.untitledcreaturemod.creature.pelican.PelicanModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PelicanEntityRenderer extends GeoEntityRenderer<PelicanEntity> {
    public PelicanEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new PelicanModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public Identifier getTexture(PelicanEntity arg) {
        return getTexture(arg);
    }
}
