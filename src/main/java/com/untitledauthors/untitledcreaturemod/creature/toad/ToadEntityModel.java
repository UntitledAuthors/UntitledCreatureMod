package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.untitledauthors.untitledcreaturemod.Utils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class ToadEntityModel extends AnimatedGeoModel<ToadEntity> {
    @Override
    public ResourceLocation getModelLocation(ToadEntity toadEntity) {
        return new ResourceLocation(MODID, "geo/toad.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ToadEntity toadEntity) {
        return new ResourceLocation(MODID, "textures/entity/toad/toad.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ToadEntity toadEntity) {
        return new ResourceLocation(MODID, "animations/entity/toad.animation.json");
    }

    @Override
    public void setLivingAnimations(ToadEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);

        // Apply head look to model
        IBone head = this.getAnimationProcessor().getBone("head");
        head.setRotationY((float) Math.toRadians(Utils.clamp(data.netHeadYaw, -45, 45)));
        head.setRotationX(-(float) Math.toRadians(data.headPitch));
    }
}
