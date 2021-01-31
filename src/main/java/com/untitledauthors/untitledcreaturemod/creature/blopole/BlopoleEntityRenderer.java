package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.untitledauthors.untitledcreaturemod.creature.GeoMobRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class BlopoleEntityRenderer extends GeoMobRenderer<BlopoleEntity> {
    public BlopoleEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BlopoleEntityModel());
        this.shadowSize = 0.5F;
    }

    @Override
    public void renderLate(BlopoleEntity animatable, MatrixStack stack, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        // TODO: Make it render a blockstate owned by the blopole entity

    }

    @Override
    public void render(BlopoleEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        if (entity.hasFlowerpot()) {
            Minecraft mc = Minecraft.getInstance();
            stack.push();

            BlockState state = Blocks.FLOWER_POT.getDefaultState();
            if (!entity.getFlowerpotContents().isEmpty()) {
                // TODO: Maybe this can be cached somewhere?
                Block pottedFlowerBlock = FlowerPotHelper.getFullPots().get(new ResourceLocation(entity.getFlowerpotContents())).get();
                if (pottedFlowerBlock != null) {
                    state = pottedFlowerBlock.getDefaultState();
                }
            }

            IBakedModel flowerPotModel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
            mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            RenderType type = RenderType.getCutoutMipped();
            float rotationYaw = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
            float ageInTicks = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, stack, ageInTicks, rotationYaw, partialTicks);
            stack.translate(-0.5f, 0.7f, -0.5f);
            Minecraft.getInstance().getItemRenderer().renderModel(flowerPotModel, ItemStack.EMPTY, packedLightIn, getPackedOverlay(entity, 0), stack, bufferIn.getBuffer(type));
            stack.pop();
        }
    }
}
