package com.untitledauthors.untitledcreaturemod.creature.blopole;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class BlopoleEntityModel extends AnimatedGeoModel<BlopoleEntity>
{
    public static ResourceLocation NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/entity/blopole/blopole.png");
    public static ResourceLocation BROWN_TEXTURE = new ResourceLocation(MODID, "textures/entity/blopole/blopole_brown.png");

    @Override
    public ResourceLocation getModelLocation(BlopoleEntity blopoleEntity) {
        return new ResourceLocation(MODID, "geo/blopole.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BlopoleEntity blopoleEntity) {
        return blopoleEntity.isBrownVariant() ? BROWN_TEXTURE : NORMAL_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BlopoleEntity blopoleEntity) {
        return new ResourceLocation(MODID, "animations/entity/blopole.animation.json");
    }

    @Override
    public void setLivingAnimations(BlopoleEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isChild()) {
            IBone root = this.getAnimationProcessor().getBone("root");
            root.setScaleX(0.7f);
            root.setScaleY(0.7f);
            root.setScaleZ(0.7f);
        }
    }
}
