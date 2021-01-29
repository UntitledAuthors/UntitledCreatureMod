package com.untitledauthors.untitledcreaturemod.creature.blopole;

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
import net.minecraft.item.crafting.Ingredient;
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

public class BlopoleEntity extends AnimalEntity implements IAnimatable
{
    private final AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle01");
    public static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static Item BREEDING_ITEM = Items.SEA_PICKLE;

    public BlopoleEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 7.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        // TODO: Come up with alternative moving predicate?
        //       The default one doesn't seen to work with slow movement speeds.

        boolean isMoving = !(limbSwingAmount > -0.05) || !(limbSwingAmount < 0.05);
        AnimationBuilder anim = isMoving ? WALK_ANIM : IDLE_ANIM;
        controller.setAnimation(anim);

        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.00, Ingredient.fromItems(BREEDING_ITEM), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 1.0D)); // TODO: Lazy random walk?
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
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
        return Registration.BLOPOLE.get().create(p_241840_1_);
    }
}
