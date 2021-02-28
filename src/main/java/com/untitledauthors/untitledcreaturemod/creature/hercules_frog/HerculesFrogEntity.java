package com.untitledauthors.untitledcreaturemod.creature.hercules_frog;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HerculesFrogEntity extends CreatureEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    public static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    public static final AnimationBuilder IDLE_SWIM_ANIM = new AnimationBuilder().addAnimation("swim_idle");
    public static final AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim");
    public static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");

    public static final AnimationBuilder STUNNED_ANIM = new AnimationBuilder().addAnimation("stun");
    public static final AnimationBuilder CHARGE_ANIM = new AnimationBuilder().addAnimation("charge");
    public static final AnimationBuilder INFLATE_ANIM = new AnimationBuilder().addAnimation("inflate");
    public static final AnimationBuilder FALL_ANIM = new AnimationBuilder().addAnimation("fall");
    public static final AnimationBuilder FLOP_ANIM = new AnimationBuilder().addAnimation("flop");

    public HerculesFrogEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                .createMutableAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));

        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    private AnimationBuilder chooseAnimation() {
        boolean isInWater = isInWater();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.02) || !(limbSwingAmount < 0.02) : !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        if (isMoving) {
            return isInWater ? SWIM_ANIM : WALK_ANIM;
        } else {
            return isInWater ? IDLE_SWIM_ANIM : IDLE_ANIM;
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.setAnimation(chooseAnimation());
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
