package net.untitledcreaturemod.creature.pelican;

import net.minecraft.util.Identifier;
import net.untitledcreaturemod.Utils;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static net.untitledcreaturemod.UntitledCreatureMod.MOD_ID;

public class PelicanModel extends AnimatedGeoModel<PelicanEntity>
{
    public static Identifier NORMAL_TEXTURE = new Identifier(MOD_ID, "textures/entity/pelican/pelican_empty.png");

    @Override
    public Identifier getModelLocation(PelicanEntity entity) {
        return new Identifier(MOD_ID, "geo/pelican.geo.json");
    }

    @Override
    public Identifier getTextureLocation(PelicanEntity entity) {
        return NORMAL_TEXTURE;
    }

    @Override
    public Identifier getAnimationFileLocation(PelicanEntity entity) {
        return new Identifier(MOD_ID, "animations/entity/pelican.animation.json");
    }

    @Override
    public void setLivingAnimations(PelicanEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isBaby()) {
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
