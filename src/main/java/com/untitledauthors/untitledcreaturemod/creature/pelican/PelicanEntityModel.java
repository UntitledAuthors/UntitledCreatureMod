package com.untitledauthors.untitledcreaturemod.creature.pelican;

import com.untitledauthors.untitledcreaturemod.Utils;
import com.untitledauthors.untitledcreaturemod.creature.hercules_frog.HerculesFrogEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class PelicanEntityModel extends AnimatedGeoModel<PelicanEntity>
{
    public static ResourceLocation NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/entity/pelican/pelican_empty.png");

    @Override
    public ResourceLocation getModelLocation(PelicanEntity blopoleEntity) {
        return new ResourceLocation(MODID, "geo/pelican.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(PelicanEntity blopoleEntity) {
        return NORMAL_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(PelicanEntity blopoleEntity) {
        return new ResourceLocation(MODID, "animations/entity/pelican.animation.json");
    }

    @Override
    public void setLivingAnimations(PelicanEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isChild()) {
            IBone root = this.getAnimationProcessor().getBone("root");
            if (root != null) {
                root.setScaleX(0.7f);
                root.setScaleY(0.7f);
                root.setScaleZ(0.7f);
            }
        }

        // Apply head look to model
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);
        IBone head = this.getAnimationProcessor().getBone("head");
        head.setRotationY((float) Math.toRadians(Utils.clamp(data.netHeadYaw, -45, 45)));
        head.setRotationX(-(float) Math.toRadians(data.headPitch));
    }
}
