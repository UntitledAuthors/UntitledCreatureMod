package com.untitledauthors.untitledcreaturemod.creature;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;


/**
 * This renderer extends GeoEntityRenderer to render leashes for Geckolib Entities by using vanillas code from MobRenderer.
 * ALl rights for the leash code belong to Mojang/original authors.
 * @param <T> The entity type to render
 */
public class GeoMobRenderer<T extends MobEntity & IAnimatable> extends GeoEntityRenderer<T> {
    protected GeoMobRenderer(EntityRendererManager renderManager, AnimatedGeoModel modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public boolean canRenderName(T entity) {
        if (entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName()) {
            return true;
        }
        return super.canRenderName(entity);
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        Entity entity = entityIn.getLeashHolder();
        if (entity != null) {
            this.renderLeash(entityIn, partialTicks, matrixStackIn, bufferIn, entity);
        }
    }

    private <E extends Entity> void renderLeash(T entityLivingIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, E leashHolder) {
        matrixStackIn.push();
        Vector3d vector3d = leashHolder.getLeashPosition(partialTicks);
        double leashYaw = (double)(MathHelper.lerp(partialTicks, entityLivingIn.renderYawOffset, entityLivingIn.prevRenderYawOffset) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        Vector3d leashPoint = entityLivingIn.func_241205_ce_();
        double leashX = Math.cos(leashYaw) * leashPoint.z + Math.sin(leashYaw) * leashPoint.x;
        double leashY = Math.sin(leashYaw) * leashPoint.z - Math.cos(leashYaw) * leashPoint.x;
        double d3 = MathHelper.lerp(partialTicks, entityLivingIn.prevPosX, entityLivingIn.getPosX()) + leashX;
        double d4 = MathHelper.lerp(partialTicks, entityLivingIn.prevPosY, entityLivingIn.getPosY()) + leashPoint.y;
        double d5 = MathHelper.lerp(partialTicks, entityLivingIn.prevPosZ, entityLivingIn.getPosZ()) + leashY;
        matrixStackIn.translate(leashX, leashPoint.y, leashY);
        float f = (float)(vector3d.x - d3);
        float f1 = (float)(vector3d.y - d4);
        float f2 = (float)(vector3d.z - d5);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos mobPos = new BlockPos(entityLivingIn.getEyePosition(partialTicks));
        BlockPos leashHolderPos = new BlockPos(leashHolder.getEyePosition(partialTicks));
        int mobBlockLight = this.getBlockLight(entityLivingIn, mobPos);
        int leashHolderBlockLight = leashHolder.isBurning() ? 15 : leashHolder.world.getLightFor(LightType.BLOCK, leashHolderPos);
        int mobSkyLight = entityLivingIn.world.getLightFor(LightType.SKY, mobPos);
        int leashHolderSkyLight = entityLivingIn.world.getLightFor(LightType.SKY, leashHolderPos);
        renderSide(ivertexbuilder, matrix4f, f, f1, f2, mobBlockLight, leashHolderBlockLight, mobSkyLight, leashHolderSkyLight, 0.025F, 0.025F, f5, f6);
        renderSide(ivertexbuilder, matrix4f, f, f1, f2, mobBlockLight, leashHolderBlockLight, mobSkyLight, leashHolderSkyLight, 0.025F, 0.0F, f5, f6);
        matrixStackIn.pop();
    }

    public static void renderSide(IVertexBuilder bufferIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
        for(int j = 0; j < 24; ++j) {
            float f = (float)j / 23.0F;
            int lerpedBlockLight = (int)MathHelper.lerp(f, (float)blockLight, (float)holderBlockLight);
            int lerpedSkyLight = (int)MathHelper.lerp(f, (float)skyLight, (float)holderSkyLight);
            int packedLight = LightTexture.packLight(lerpedBlockLight, lerpedSkyLight);
            addVertexPair(bufferIn, matrixIn, packedLight, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
            addVertexPair(bufferIn, matrixIn, packedLight, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
        }
    }

    public static void addVertexPair(IVertexBuilder bufferIn, Matrix4f matrixIn, int packedLight, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
        float red = 0.5F;
        float green = 0.4F;
        float blue = 0.3F;
        if (p_229120_9_ % 2 == 0) {
            red *= 0.7F;
            green *= 0.7F;
            blue *= 0.7F;
        }

        float f3 = (float)p_229120_9_ / (float)p_229120_8_;
        float f4 = p_229120_3_ * f3;
        float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
        float f6 = p_229120_5_ * f3;
        if (!p_229120_10_) {
            bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(red, green,  blue, 1.0F).lightmap(packedLight).endVertex();
        }

        bufferIn.pos(matrixIn, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(red, green,  blue, 1.0F).lightmap(packedLight).endVertex();
        if (p_229120_10_) {
            bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(red, green,  blue, 1.0F).lightmap(packedLight).endVertex();
        }
    }
}
