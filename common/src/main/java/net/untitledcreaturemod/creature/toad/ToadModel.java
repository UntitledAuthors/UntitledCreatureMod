package net.untitledcreaturemod.creature.toad;

import net.minecraft.util.Identifier;
import static net.untitledcreaturemod.UntitledCreatureMod.MOD_ID;

import net.untitledcreaturemod.Utils;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class ToadModel extends AnimatedGeoModel<ToadEntity> {
    @Override
    public Identifier getModelLocation(ToadEntity toadEntity) {
        return new Identifier(MOD_ID, "geo/toad.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ToadEntity toadEntity) {
        return new Identifier(MOD_ID, "textures/entity/toad/toad.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ToadEntity toadEntity) {
        return new Identifier(MOD_ID, "animations/entity/toad.animation.json");
    }

    @Override
    public void setLivingAnimations(ToadEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData data = (EntityModelData) customPredicate.getExtraData().get(0);
        if (entity.isBaby()) {
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
