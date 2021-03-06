package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.untitledauthors.untitledcreaturemod.creature.common.*;
import com.untitledauthors.untitledcreaturemod.creature.toad.ai.AttackOnceGoal;
import com.untitledauthors.untitledcreaturemod.creature.toad.ai.HurtByTargetOnceGoal;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ToadEntity extends AnimalEntity implements IAnimatable, BucketCreature, FleeingCreature {
    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(ToadEntity.class, DataSerializers.BOOLEAN);
    public static final String FROM_BUCKET_TAG = "fromBucket";
    private static final int FLEE_DURATION_S = 30;
    private final AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    public static AnimationBuilder IDLE_SWIM_ANIM = new AnimationBuilder().addAnimation("idle_swim");
    public static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim");
    public static Item BREEDING_ITEM = Items.SPIDER_EYE;

    private LivingEntity fleeTarget;
    /// Number of ticks since the fleeing started
    private int fleeTargetTimestamp = 0;

    public ToadEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    @Nonnull
    protected PathNavigator createNavigator(@Nonnull World world) {
        return new WalkAndSwimNavigator(this, world);
    }

    @Override
    public int getMaxAir() {
        return 900;
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 7.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                .createMutableAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.0D);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<ToadEntity> controller = event.getController();
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
        this.goalSelector.addGoal(0, new CreatureBreatheAirGoal(this));
        this.goalSelector.addGoal(1, new AttackOnceGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new CreatureFleeGoal<>(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new CreatureTemptGoal(this, 1.1D, BREEDING_ITEM));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetOnceGoal(this));
    }

    /// Return poison duration in ticks, based on difficulty
    private static int poisonDuration(Difficulty difficulty) {
        switch (difficulty) {
            case NORMAL:
                return 7 * 20;
            case HARD:
                return 15 * 20;
        }
        return 3 * 20;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        // Mostly copied from CaveSpider
        if (super.attackEntityAsMob(entityIn)) {
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, poisonDuration(world.getDifficulty()), 0));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
        super.damageEntity(damageSrc, damageAmount);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        // Toad is immune to poison effect
        if (potioneffectIn.getPotion() == Effects.POISON) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        if (source.getImmediateSource() instanceof PoisonousSecretionsEntity) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (world.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (source instanceof EntityDamageSource) {
            EntityDamageSource entityDamageSource = (EntityDamageSource) source;
            Entity attacker = entityDamageSource.getTrueSource();
            if (attacker == null) {
                return super.attackEntityFrom(source, amount);
            }

            // TODO: Clean this up maybe
            boolean alert = true;
            if (attacker instanceof PlayerEntity) {
                if (((PlayerEntity) attacker).isCreative()) {
                    alert = false;
                }
            }
            if (alert) {
                alertOthersToFlee((LivingEntity)attacker);
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        // Stop fleeing after short amount of time
        if (fleeTarget != null) {
            if ((this.ticksExisted - this.fleeTargetTimestamp) > FLEE_DURATION_S*20) {
                this.setFleeTarget(null);
            }
        }
    }

    public LivingEntity getAttackingEntity() {
        return this.fleeTarget;
    }

    @Override
    public boolean shouldFlee() {
        return true;
    }

    public void setFleeTarget(LivingEntity fleeTarget) {
        this.fleeTargetTimestamp = this.ticksExisted;
        this.fleeTarget = fleeTarget;
    }

    // Called on right clicking the toad/entity
    // Mostly copied from AbstractFishEntity
    @Nonnull
    public ActionResultType func_230254_b_(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.getItem() == Items.BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack creatureBucket = new ItemStack(Registration.TOAD_BUCKET.get());
            CompoundNBT itemNBT = new CompoundNBT();
            itemNBT.put("EntityTag", serializeNBT());
            creatureBucket.setTag(itemNBT);

            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, creatureBucket);
            }

            if (itemstack.isEmpty()) {
                player.setHeldItem(hand, creatureBucket);
            } else if (!player.inventory.addItemStackToInventory(creatureBucket)) {
                player.dropItem(creatureBucket, false);
            }

            this.remove();
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else {
            return super.func_230254_b_(player, hand);
        }
    }

    protected void alertOthersToFlee(LivingEntity attacker) {
        double alertRadius = getAttributeValue(Attributes.FOLLOW_RANGE);
        AxisAlignedBB alertBox = AxisAlignedBB.fromVector(getPositionVec()).grow(alertRadius, 10.0D, alertRadius);
        List<ToadEntity> list = world.getLoadedEntitiesWithinAABB(ToadEntity.class, alertBox);
        for (ToadEntity buddy : list) {
            buddy.setFleeTarget(attacker);
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
    public AgeableEntity func_241840_a(@Nonnull  ServerWorld p_241840_1_, @Nonnull  AgeableEntity p_241840_2_) {
        return Registration.TOAD.get().create(p_241840_1_);
    }

    // TODO: Exchange these once we got sounds, even though the cod ones sound kinda appropriate
    protected SoundEvent getAmbientSound() {
        return Registration.TOAD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(FROM_BUCKET, false);
    }

    public boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean isFromBucket) {
        this.dataManager.set(FROM_BUCKET, isFromBucket);
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isFromBucket();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean(FROM_BUCKET_TAG, isFromBucket());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setFromBucket(compound.getBoolean(FROM_BUCKET_TAG));
    }

    @Override
    public boolean shouldJumpWhileFleeing() {
        return true;
    }
}
