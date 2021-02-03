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
import software.bernie.geckolib3.geo.render.built.GeoBone;

import javax.annotation.Nullable;

public class BlopoleEntityRenderer extends GeoMobRenderer<BlopoleEntity> {
    private IRenderTypeBuffer rtb;
    private ResourceLocation whTexture;

    public BlopoleEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BlopoleEntityModel());
        this.shadowSize = 0.5F;
    }

    @Override
    public void renderEarly(BlopoleEntity animatable, MatrixStack stackIn, float ticks, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        //store the Render Type Buffer and current texture, we'll need them later.
        this.rtb = renderTypeBuffer;
        this.whTexture = this.getTextureLocation(animatable);
        this.entityBeingRendered = animatable;
    }

    private BlopoleEntity entityBeingRendered;

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("pot") && entityBeingRendered.hasFlowerpot()) {
            Minecraft mc = Minecraft.getInstance();
            stack.push();

            BlockState state = Blocks.FLOWER_POT.getDefaultState();
            if (!entityBeingRendered.getFlowerpotContents().isEmpty()) {
                // TODO: Maybe this can be cached somewhere?
                Block pottedFlowerBlock = FlowerPotHelper.getFullPots().get(new ResourceLocation(entityBeingRendered.getFlowerpotContents())).get();
                if (pottedFlowerBlock != null) {
                    state = pottedFlowerBlock.getDefaultState();
                }
            }

            IBakedModel flowerPotModel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
            mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            RenderType type = RenderType.getCutoutMipped();
            stack.translate(-0.5f, 0.7f, -0.5f);
            Minecraft.getInstance().getItemRenderer().renderModel(flowerPotModel, ItemStack.EMPTY, packedLightIn, getPackedOverlay(entityBeingRendered, 0), stack, rtb.getBuffer(type));

            stack.pop();
            bufferIn = rtb.getBuffer(RenderType.getEntitySmoothCutout(whTexture));
        }

        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(BlopoleEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntityCutoutNoCull(textureLocation);
    }
}
