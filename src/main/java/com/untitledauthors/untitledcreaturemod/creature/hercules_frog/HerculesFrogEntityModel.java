package com.untitledauthors.untitledcreaturemod.creature.hercules_frog;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class HerculesFrogEntityModel extends AnimatedGeoModel<HerculesFrogEntity>
{
    public static ResourceLocation NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/entity/hercules_frog/hercules_frog.png");

    @Override
    public ResourceLocation getModelLocation(HerculesFrogEntity blopoleEntity) {
        return new ResourceLocation(MODID, "geo/hercules_frog.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HerculesFrogEntity blopoleEntity) {
        return NORMAL_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HerculesFrogEntity blopoleEntity) {
        return new ResourceLocation(MODID, "animations/entity/hercules_frog.animation.json");
    }

    @Override
    public void setLivingAnimations(HerculesFrogEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isChild()) {
            IBone root = this.getAnimationProcessor().getBone("root");
            root.setScaleX(0.7f);
            root.setScaleY(0.7f);
            root.setScaleZ(0.7f);
        }
    }
}
