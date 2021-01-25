package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.untitledauthors.untitledcreaturemod.Utils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class BlopoleEntityModel extends AnimatedGeoModel<BlopoleEntity>
{
    @Override
    public ResourceLocation getModelLocation(BlopoleEntity blopoleEntity) {
        return new ResourceLocation(MODID, "geo/blopole.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BlopoleEntity blopoleEntity) {
        return new ResourceLocation(MODID, "textures/entity/blopole/blopole.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BlopoleEntity blopoleEntity) {
        return new ResourceLocation(MODID, "animations/entity/blopole.animation.json");
    }

    @Override
    public void setLivingAnimations(BlopoleEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);
        if (entity.isChild()) {
            IBone root = this.getAnimationProcessor().getBone("bodymain");
            root.setScaleX(0.7f);
            root.setScaleY(0.7f);
            root.setScaleZ(0.7f);
        }

        // Apply head look to model
        IBone head = this.getAnimationProcessor().getBone("head");
        head.setRotationY((float) Math.toRadians(Utils.clamp(data.netHeadYaw, -45, 45)));
        head.setRotationX(-(float) Math.toRadians(data.headPitch));
    }
}
