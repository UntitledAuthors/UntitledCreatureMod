package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import com.untitledauthors.untitledcreaturemod.creature.common.CreatureFleeGoal;
import com.untitledauthors.untitledcreaturemod.creature.common.DirectedFleeingCreature;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
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
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
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
import java.util.List;

public class RockAntelopeEntity extends AnimalEntity implements IAnimatable, DirectedFleeingCreature {

    private final AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    public static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static AnimationBuilder GRAZING_ANIM = new AnimationBuilder().addAnimation("grazing");
    public static AnimationBuilder JOUSTING_ANIM = new AnimationBuilder().addAnimation("joust");
    public static AnimationBuilder RUN_ANIM = new AnimationBuilder().addAnimation("run");

    // This byte contains information about whether antelope is a leader and which horns are grown
    public static DataParameter<Byte> STATE = EntityDataManager.createKey(RockAntelopeEntity.class, DataSerializers.BYTE);
    public static final String STATE_TAG = "state";
    public static final byte RIGHT_HORN_MASK = 0x01; // rightmost bit set, if right horn present
    public static final byte LEFT_HORN_MASK = 0x02; // second bit set, if left horn present
    public static final byte LEADER_MASK = 0x04; // third bit set if antelope is leader
    public static final byte ATTACKING_MASK = 0x08; // fourth bit set if antelope is attacking

    // TODO: Since client don't need to know about the id maybe can remove the parameter and move isJousting to STATE
    public static DataParameter<Integer> JOUSTING_PARTNER_ID = EntityDataManager.createKey(RockAntelopeEntity.class, DataSerializers.VARINT);

    private static final int FLEE_DURATION_S = 30;
    public static Item BREEDING_ITEM = Items.ACACIA_LEAVES;
    private EatGrassGoal eatGrassGoal;
    private int eatGrassTimer = -30;
    private Vector3d commonFleeTarget;
    private LivingEntity attackingEntity;
    /// Number of ticks since the fleeing started
    private int fleeTargetTimestamp = 0;
    private boolean wasAttacking = false;

    public RockAntelopeEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 5.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0F)
                .createMutableAttribute(Attributes.ATTACK_SPEED, 6.0F)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 3.0F);
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

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (world.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (source instanceof EntityDamageSource) {
            EntityDamageSource entityDamageSource = (EntityDamageSource) source;
            Entity attacker = entityDamageSource.getTrueSource();

            // TODO: Clean this up maybe
            boolean alert = true;
            if (attacker instanceof PlayerEntity) {
                if (((PlayerEntity) attacker).isCreative()) {
                    alert = false;
                }
            }
            if (alert) {
                Vector3d attackerPos = attacker.getPositionVec();

                for (int i = 0; i < 20; i++) {
                    commonFleeTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 32, 7, attackerPos);
                    if (commonFleeTarget != null) {
                        System.out.printf("Found random position after %d iterations\n", i);
                        break;
                    }
                }

                System.out.printf("Alert others to flee to %s\n", commonFleeTarget);
                setCommonFleeTarget(commonFleeTarget);
                alertOthersToFlee((LivingEntity)attacker, commonFleeTarget);
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    public void alertOthersToFlee(LivingEntity attacker, Vector3d commonFleeTarget) {
        double alertRadius = getAttributeValue(Attributes.FOLLOW_RANGE);
        AxisAlignedBB alertBox = AxisAlignedBB.fromVector(getPositionVec()).grow(alertRadius, 16.0D, alertRadius);
        List<RockAntelopeEntity> list = world.getLoadedEntitiesWithinAABB(RockAntelopeEntity.class, alertBox);
        for (RockAntelopeEntity buddy : list) {
            buddy.setAttackingEntity(attacker);
            buddy.setCommonFleeTarget(commonFleeTarget);
            // Make leader defend the herd
            if (buddy.isLeader()) {
                buddy.setAttackTarget(attacker);
                buddy.setRevengeTarget(attacker);
            }
        }
    }

    public void setAttackingEntity(LivingEntity attackingEntity) {
        this.fleeTargetTimestamp = this.ticksExisted;
        this.attackingEntity = attackingEntity;
    }

    @SuppressWarnings("rawtypes")
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
        boolean isMoving = !(limbSwingAmount > -0.10F) || !(limbSwingAmount < 0.10F);
        boolean isRunning = isMoving && !(limbSwingAmount > -0.9F) || !(limbSwingAmount < 0.9F);

        if (isRunning) {
            controller.setAnimation(RUN_ANIM);
            return PlayState.CONTINUE;
        } else if(isMoving) {
            controller.setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            controller.setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    protected void registerGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.addGoal(0, new SwimGoal(this));

        this.goalSelector.addGoal(1, new DefendHerdGoal(this, 1.35, false));
        this.goalSelector.addGoal(1, new CreatureFleeGoal<>(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.20D, Ingredient.fromItems(BREEDING_ITEM), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.20D));
        this.goalSelector.addGoal(5, new FollowLeaderGoal(this, 1.35D));
        this.goalSelector.addGoal(5, eatGrassGoal);

        this.goalSelector.addGoal(5, new JoustGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new StartJoustGoal(this, 1.0D));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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
        if (world.isRemote) {
            return;
        }
        this.setHealth(this.getHealth() + 1.0f);

        // Grow back single horn on eating grass
        if (!getLeftHornPresent()) {
            setLeftHornPresent(true);
            return;
        }
        if (!getRightHornPresent()) {
            setRightHornPresent(true);
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

    @Override
    public void tick() {
        super.tick();
        if (attackingEntity != null) {
            if ((this.ticksExisted - this.fleeTargetTimestamp) > FLEE_DURATION_S*20) {
                this.setAttackingEntity(null);
            }
        }

        if (isLeader()) {
            if (getAttackTarget() != null) {
                if (!wasAttacking) {
                    System.out.println("Start attacking!");
                    setIsAttacking(true);
                    wasAttacking = true;
                }
            } else {
                if (wasAttacking) {
                    System.out.println("Stop attacking");
                    setIsAttacking(false);
                    wasAttacking = false;
                }
            }
        }

        // TODO: Remove this after debugging jousting alignment
        // setCustomNameVisible(true);
        // setCustomName(new StringTextComponent(String.format("Antelope %d", this.getEntityId())));
    }

    @Nullable
    @Override
    // This returns a new baby entity for breeding
    public AgeableEntity func_241840_a(@Nonnull ServerWorld p_241840_1_, @Nonnull AgeableEntity p_241840_2_) {
        return Registration.ROCK_ANTELOPE.get().create(p_241840_1_);
    }

    public int getJoustingPartner() {
        return this.dataManager.get(JOUSTING_PARTNER_ID);
    }

    public void setJoustingPartner(int joustingPartnerId) {
        this.dataManager.set(JOUSTING_PARTNER_ID, joustingPartnerId);
    }

    public byte getState() {
        return dataManager.get(STATE);
    }

    public void setState(byte state) {
        dataManager.set(STATE, state);
    }

    public int getNumberOfHorns() {
        byte state = dataManager.get(STATE);
        boolean leftHornPresent = (state & LEFT_HORN_MASK) > 0;
        boolean rightHornPresent = (state & RIGHT_HORN_MASK) > 0;
        return (leftHornPresent ? 1 : 0) + (rightHornPresent ? 1 : 0);
    }

    public boolean canJoust() {
        return !this.isChild() && this.getNumberOfHorns() == 2;
    }

    public boolean getLeftHornPresent() {
        return getStateBit(LEFT_HORN_MASK);
    }

    public boolean getRightHornPresent() {
        return getStateBit(RIGHT_HORN_MASK);
    }

    private void setStateBit(byte mask, boolean isSet) {
        byte state = getState();
        if (isSet) {
            setState((byte) (state | mask));
        } else {
            setState((byte) (state & ~mask));
        }
    }

    private boolean getStateBit(byte mask) {
        return (getState() & mask) > 0;
    }

    public void setLeftHornPresent(boolean present) {
        setStateBit(LEFT_HORN_MASK, present);
    }

    public void setRightHornPresent(boolean present) {
        setStateBit(RIGHT_HORN_MASK, present);
    }

    public void setIsAttacking(boolean isAttacking) {
        setStateBit(ATTACKING_MASK, isAttacking);
    }

    public boolean isAttacking() {
        return getStateBit(ATTACKING_MASK);
    }

    public boolean isLeader() {
        return getStateBit(LEADER_MASK);
    }

    public void setIsLeader(boolean value) {
        setStateBit(LEADER_MASK, value);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(JOUSTING_PARTNER_ID, 0);
        this.dataManager.register(STATE, (byte)0x03); // 0x03 -> have both horns but not be leader
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte(STATE_TAG, getState());
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        setState(compound.getByte(STATE_TAG));
    }

    @Override
    public LivingEntity getAttackingEntity() {
        return attackingEntity;
    }

    @Override
    public boolean shouldFlee() {
        return !isLeader();
    }

    @Override
    public void setCommonFleeTarget(Vector3d target) {
        commonFleeTarget = target;
    }

    @Override
    public Vector3d getCommonFleeTarget() {
        return commonFleeTarget;
    }
}
