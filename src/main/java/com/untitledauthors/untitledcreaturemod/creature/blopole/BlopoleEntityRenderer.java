package com.untitledauthors.untitledcreaturemod.creature.blopole;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;



public class BlopoleEntityRenderer extends GeoEntityRenderer<BlopoleEntity> {
    public BlopoleEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BlopoleEntityModel());
        this.shadowSize = 0.8F;
    }

}
