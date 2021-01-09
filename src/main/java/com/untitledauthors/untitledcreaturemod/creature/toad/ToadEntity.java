package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class ToadEntity extends AnimalEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    public static AnimationBuilder IDLE_SWIM_ANIM = new AnimationBuilder().addAnimation("idle_swim");
    public static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim");
    public static Item BREEDING_ITEM = Items.SPIDER_EYE;


    public ToadEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new ToadEntity.Navigator(this, worldIn);
    }

    @Override
    public int getMaxAir() {
        return 600;
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0F);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        // TODO: Come up with alternative moving predicate?
        //       The default one doesn't seen to work with slow movement speeds.
        boolean isInWater = isInWater();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.02) || !(limbSwingAmount < 0.02) : !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        AnimationBuilder anim = isInWater ? IDLE_SWIM_ANIM : IDLE_ANIM;
        if (isMoving) {
            anim = isInWater ? SWIM_ANIM : WALK_ANIM;
        }
        controller.setAnimation(anim);

        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ToadBreatheAirGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new ToadTemptGoal(this, 1.1D, BREEDING_ITEM));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    static class Navigator extends SwimmerPathNavigator {
        Navigator(ToadEntity turtle, World worldIn) {
            super(turtle, worldIn);
        }

        protected boolean canNavigate() {
            return true;
        }

        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkAndSwimNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }
    }


    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == BREEDING_ITEM;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        // TODO: Investigate what this is used for, breeding maybe?
        return Registration.TOAD.get().create(p_241840_1_);
    }

    // TODO: Exchange these once we got sounds, even though the cod ones sound kinda appropriate
    protected SoundEvent getAmbientSound() {
        return Registration.TOAD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }
}
