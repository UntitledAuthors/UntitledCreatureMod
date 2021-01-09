package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import com.untitledauthors.untitledcreaturemod.Utils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class RockAntelopeEntityModel extends AnimatedGeoModel<RockAntelopeEntity> {
    @Override
    public ResourceLocation getModelLocation(RockAntelopeEntity rockAntelopeEntity) {
        return new ResourceLocation(MODID, "geo/rock_antelope.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RockAntelopeEntity rockAntelopeEntity) {
        return new ResourceLocation(MODID, "textures/entity/rock_antelope/rock_antelope.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RockAntelopeEntity rockAntelopeEntity) {
        return new ResourceLocation(MODID, "animations/entity/rock_antelope.animation.json");
    }

    @Override
    public void setLivingAnimations(RockAntelopeEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);

        // Apply head look to model
        IBone head = this.getAnimationProcessor().getBone("Neck");
        head.setRotationY((float) Math.toRadians(Utils.clamp(data.netHeadYaw, -45, 45)));
        head.setRotationX(-(float) Math.toRadians(data.headPitch));
    }
}
