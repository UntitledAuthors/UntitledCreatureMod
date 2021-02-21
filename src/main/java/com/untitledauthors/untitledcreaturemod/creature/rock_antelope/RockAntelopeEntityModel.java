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
    public static ResourceLocation NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/entity/rock_antelope/rock_antelope.png");
    public static ResourceLocation LEADER_TEXTURE = new ResourceLocation(MODID, "textures/entity/rock_antelope/rock_antelope_leader.png");
    private static final float LEADER_HORN_SCALE_FACTOR = 1.25f;

    @Override
    public ResourceLocation getModelLocation(RockAntelopeEntity rockAntelopeEntity) {
        return new ResourceLocation(MODID, "geo/rock_antelope.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RockAntelopeEntity entity) {
        return entity.isLeader() ? LEADER_TEXTURE : NORMAL_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RockAntelopeEntity rockAntelopeEntity) {
        return new ResourceLocation(MODID, "animations/entity/rock_antelope.animation.json");
    }

    @Override
    public void setLivingAnimations(RockAntelopeEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone leftHorn = this.getAnimationProcessor().getBone("LeftHorn");
        IBone rightHorn = this.getAnimationProcessor().getBone("RightHorn");
        if (entity.isChild()) {
            leftHorn.setHidden(true);
            rightHorn.setHidden(true);
            IBone root = this.getAnimationProcessor().getBone("MainBody");
            root.setScaleX(0.7f);
            root.setScaleY(0.7f);
            root.setScaleZ(0.7f);
        } else {
            leftHorn.setHidden(!entity.getLeftHornPresent());
            rightHorn.setHidden(!entity.getRightHornPresent());
        }
        if (entity.isLeader()) {
            leftHorn.setScaleX(LEADER_HORN_SCALE_FACTOR);
            leftHorn.setScaleY(LEADER_HORN_SCALE_FACTOR);
            leftHorn.setScaleZ(LEADER_HORN_SCALE_FACTOR);
            rightHorn.setScaleX(LEADER_HORN_SCALE_FACTOR);
            rightHorn.setScaleY(LEADER_HORN_SCALE_FACTOR);
            rightHorn.setScaleZ(LEADER_HORN_SCALE_FACTOR);
        }

        // Don't rotate head in code when playing these animations
        if (entity.isEatingGrass() || entity.getJoustingPartner() != 0) {
            return;
        }
        // Apply head look to model
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);
        IBone head = this.getAnimationProcessor().getBone("Neck");
        head.setRotationY((float) Math.toRadians(Utils.clamp(data.netHeadYaw, -45, 45)));
        head.setRotationX((float) Math.toRadians(data.headPitch));
    }
}
