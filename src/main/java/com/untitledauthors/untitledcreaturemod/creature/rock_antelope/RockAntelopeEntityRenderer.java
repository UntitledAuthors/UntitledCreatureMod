package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RockAntelopeEntityRenderer extends GeoMobRenderer<RockAntelopeEntity> {
    public RockAntelopeEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new RockAntelopeEntityModel());
        this.shadowSize = 0.8F;
    }

    @Override
    public RenderType getRenderType(RockAntelopeEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntityCutoutNoCull(textureLocation);
    }
}
