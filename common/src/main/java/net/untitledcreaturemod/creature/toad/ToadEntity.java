package net.untitledcreaturemod.creature.toad;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.untitledcreaturemod.creature.ai.CreatureBreatheAirGoal;
import net.untitledcreaturemod.creature.ai.CreatureFleeGoal;
import net.untitledcreaturemod.creature.ai.CreatureTemptGoal;
import net.untitledcreaturemod.creature.ai.FleeingCreature;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

public class ToadEntity extends AnimalEntity implements IAnimatable, FleeingCreature {
    private static int MAX_AIR = 900;

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

    protected ToadEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    public static DefaultAttributeContainer.Builder getDefaultAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 7.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D);
        // TODO: Swim Speed Mixin
    }

    @Override
    public int getMaxAir() {
        return MAX_AIR;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        // return Registration.TOAD.get().create(p_241840_1_);
        // TODO: Implement after registration
        return null;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new CreatureBreatheAirGoal(this));
        // TODO: Implement hurt when player touches toad
        // this.goalSelector.add(1, new AttackOnceGoal(this, 1.25D));
        this.goalSelector.add(2, new CreatureFleeGoal<>(this, 1.5f));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new CreatureTemptGoal(this, 1.1D, BREEDING_ITEM));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
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
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        // Toad is immune to posion effect
        if (effect.getEffectType() == StatusEffects.POISON) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.getSource() instanceof PoisonousSecretionsEntity) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        // TODO: Test if this makes the player poisoned
        if (attacker instanceof PlayerEntity) {
            ((LivingEntity)attacker).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, poisonDuration(world.getDifficulty()), 0));
            return true;
        }
        return super.handleAttack(attacker);
    }

    // Flee when hit
    // TODO: Maybe move to handleAttack?
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (world.isClient) {
            return super.damage(source, amount);
        }
        if (source instanceof EntityDamageSource) {
            EntityDamageSource entityDamageSource = (EntityDamageSource) source;
            Entity attacker = entityDamageSource.getAttacker();
            if (attacker == null) {
                return super.damage(source, amount);
            }

            boolean attackerIsPlayer = attacker instanceof PlayerEntity && !((PlayerEntity) attacker).isCreative();
            if (attackerIsPlayer) {
                alertOthersToFlee((LivingEntity)attacker);
            }
        }

        return super.damage(source, amount);
    }

    protected void alertOthersToFlee(LivingEntity attacker) {
        double alertRadius = getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box alertBox = Box.method_29968(getPos()).expand(alertRadius, 10.0D, alertRadius);
        List<ToadEntity> list = world.getEntitiesIncludingUngeneratedChunks(ToadEntity.class, alertBox);
        for (ToadEntity buddy : list) {
            buddy.setFleeTarget(attacker);
        }
    }

    private void setFleeTarget(LivingEntity attacker) {
        this.fleeTargetTimestamp = this.age;
        this.fleeTarget = attacker;
    }

    @Override
    public void tick() {
        super.tick();
        // Stop fleeing after short amount of time
        if (!world.isClient && fleeTarget != null) {
            if ((this.age - this.fleeTargetTimestamp) > FLEE_DURATION_S*20) {
                this.setFleeTarget(null);
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemStack.decrement(1);
            // TODO: Uncomment after porting CreatureBucket
            // ItemStack creatureBucket = this.getFishBucketItem();
            // this.copyDataToStack(creatureBucket);
            // if (!this.world.isClient) {
            //     Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)player, creatureBucket);
            // }

            // if (itemStack.isEmpty()) {
            //     player.setStackInHand(hand, creatureBucket);
            // } else if (!player.inventory.insertStack(creatureBucket)) {
            //     player.dropItem(creatureBucket, false);
            // }

            this.remove();
            return ActionResult.success(this.world.isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == BREEDING_ITEM;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends IAnimatable> PlayState predicate(AnimationEvent<T> event) {
        AnimationController<ToadEntity> controller = event.getController();
        boolean isInWater = isTouchingWater();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.02) || !(limbSwingAmount < 0.02) : !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        AnimationBuilder anim = isInWater ? IDLE_SWIM_ANIM : IDLE_ANIM;
        if (isMoving) {
            anim = isInWater ? SWIM_ANIM : WALK_ANIM;
        }
        controller.setAnimation(anim);

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public LivingEntity getAttackingEntity() {
        return this.fleeTarget;
    }

    @Override
    public boolean shouldFlee() {
        return true;
    }

    @Override
    public boolean shouldJumpWhileFleeing() {
        return true;
    }
}
