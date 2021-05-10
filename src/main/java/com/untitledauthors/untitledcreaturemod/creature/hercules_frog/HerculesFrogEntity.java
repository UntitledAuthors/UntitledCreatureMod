package com.untitledauthors.untitledcreaturemod.creature.hercules_frog;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.w3c.dom.Attr;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.EnumSet;

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

    private boolean wasFalling = false;
    private int floppingTime = 5*60;

    public HerculesFrogEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                .createMutableAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.0D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new PanicGoal(this, 2.5D));
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));

        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0f, false));
        this.goalSelector.addGoal(10, new HerculesLeapGoal(this, 0.8F));

        //this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, TurtleEntity.class, false, TurtleEntity.TARGET_DRY_BABY));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // TODO: Maybe they shouldn't be?
        if (source == DamageSource.FALL) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    private AnimationBuilder chooseAnimation() {
        boolean isInWater = isInWater();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.02) || !(limbSwingAmount < 0.02) : !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        if (floppingTime-- > 0) {
            return FLOP_ANIM;
        }
        if (isOnGround() && wasFalling) {
            floppingTime = 5*60;
            wasFalling = !isOnGround();
            return FLOP_ANIM;
        }
        wasFalling = !isOnGround();
        if (!isOnGround()) {
            return FALL_ANIM;
        }
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
    public void applyEntityCollision(@Nonnull Entity entityIn) {
        // Don't collide with entity when jumping upon them
        // TODO: Bury player?
        if (isOnGround() || getMotion().getY() > 0) {
            super.applyEntityCollision(entityIn);
            return;
        }
        // TODO: Probably needs timeout or something
        if (entityIn instanceof LivingEntity) {
            entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0f);
            ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.NAUSEA, effectDuration(world.getDifficulty()), 0));
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        //return super.attackEntityAsMob(entityIn);
        return false;
    }


    /// Return nausea duration in ticks, based on difficulty
    private static int effectDuration(Difficulty difficulty) {
        switch (difficulty) {
            case NORMAL:
                return 3 * 20;
            case HARD:
                return 7 * 20;
        }
        return 1 * 20;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
