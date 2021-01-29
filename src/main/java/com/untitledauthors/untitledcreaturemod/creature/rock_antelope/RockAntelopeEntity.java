package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RockAntelopeEntity extends AnimalEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    public static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static AnimationBuilder GRAZING_ANIM = new AnimationBuilder().addAnimation("grazing");
    public static AnimationBuilder JOUSTING_ANIM = new AnimationBuilder().addAnimation("joust");

    public static DataParameter<Boolean> IS_LEADER = EntityDataManager.createKey(RockAntelopeEntity.class, DataSerializers.BOOLEAN);
    public static final String IS_LEADER_TAG = "IsLeader";
    public static DataParameter<Integer> JOUSTING_PARTNER_ID = EntityDataManager.createKey(RockAntelopeEntity.class, DataSerializers.VARINT);

    public static Item BREEDING_ITEM = Items.ACACIA_LEAVES;
    private EatGrassGoal eatGrassGoal;
    private int eatGrassTimer = -30;

    public RockAntelopeEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 5.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0F);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (damageSrc.getDamageType().equals("fall")) {
            // 0.75% fall damage reduction
            damageAmount *= 0.25;
        }
        super.damageEntity(damageSrc, damageAmount);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        if (getJoustingPartner() != 0) {
            controller.setAnimation(JOUSTING_ANIM);
            return PlayState.CONTINUE;
        }

        if (isEatingGrass()) {
            controller.setAnimation(GRAZING_ANIM);
            return PlayState.CONTINUE;
        }
        // TODO: Come up with alternative moving predicate?
        //       The default one doesn't seen to work with slow movement speeds.
        boolean isMoving = !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        AnimationBuilder anim = isMoving ? WALK_ANIM : IDLE_ANIM;
        controller.setAnimation(anim);

        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.addGoal(0, new SwimGoal(this));
        // TODO: Leader should alert others to attack
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromItems(BREEDING_ITEM), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new FollowLeaderGoal(this, 1.25D));
        this.goalSelector.addGoal(5, eatGrassGoal);

        this.goalSelector.addGoal(5, new JoustGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new StartJoustGoal(this, 1.0D));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    protected void updateAITasks() {
        this.eatGrassTimer = this.eatGrassGoal.getEatingGrassTimer();
        super.updateAITasks();
    }

    public void livingTick() {
        if (this.world.isRemote) {
            this.eatGrassTimer = Math.max(-30, this.eatGrassTimer - 1);
        }
        super.livingTick();
    }

    public boolean isEatingGrass() {
        // Block gets replaced at tick 4, eating animation is 2.5 seconds => 50 ticks
        return this.eatGrassTimer < 20 && this.eatGrassTimer > -30;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void eatGrassBonus() {
        if (!world.isRemote) {
            this.setHealth(this.getHealth() + 1.0f);
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
    // This returns a new baby entity for breeding
    public AgeableEntity func_241840_a(@Nonnull ServerWorld p_241840_1_, @Nonnull AgeableEntity p_241840_2_) {
        return Registration.ROCK_ANTELOPE.get().create(p_241840_1_);
    }

    public void setJoustingPartner(int joustingPartnerId) {
        this.dataManager.set(JOUSTING_PARTNER_ID, joustingPartnerId);
    }

    public int getJoustingPartner() {
        return this.dataManager.get(JOUSTING_PARTNER_ID);
    }

    public void setIsLeader(boolean value) {
        this.dataManager.set(IS_LEADER, value);
    }

    public boolean isLeader() {
        return this.dataManager.get(IS_LEADER);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(JOUSTING_PARTNER_ID, 0);
        this.dataManager.register(IS_LEADER, false);
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean(IS_LEADER_TAG, isLeader());
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        setIsLeader(compound.getBoolean(IS_LEADER_TAG));
    }
}
