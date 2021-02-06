package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.untitledauthors.untitledcreaturemod.creature.common.BucketCreature;
import com.untitledauthors.untitledcreaturemod.creature.common.CreatureBreatheAirGoal;
import com.untitledauthors.untitledcreaturemod.creature.common.CreatureTemptGoal;
import com.untitledauthors.untitledcreaturemod.creature.common.WalkAndSwimNavigator;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class BlopoleEntity extends TameableEntity implements IAnimatable, BucketCreature {
    private final AnimationFactory factory = new AnimationFactory(this);
    public static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle01");
    public static final AnimationBuilder IDLE_SIDE_ANIM = new AnimationBuilder().addAnimation("idle02");
    public static final AnimationBuilder IDLE_FLIPPED_ANIM = new AnimationBuilder().addAnimation("idle03");
    public static final AnimationBuilder IDLE_SWIM_ANIM = new AnimationBuilder().addAnimation("idle_swim");
    public static final AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim");
    public static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static final Item BREEDING_ITEM = Items.SEA_PICKLE;
    private int timeUntilNextBurp = rand.nextInt(6000) + 6000;

    public static DataParameter<Byte> CHOSEN_IDLE_ANIM = EntityDataManager.createKey(BlopoleEntity.class,
            DataSerializers.BYTE);

    public static DataParameter<Boolean> HAS_FLOWERPOT = EntityDataManager.createKey(BlopoleEntity.class,
            DataSerializers.BOOLEAN);
    public static final String HAS_FLOWERPOT_TAG = "hasFlowerpot";
    public static DataParameter<String> FLOWERPOT_CONTENTS = EntityDataManager.createKey(BlopoleEntity.class,
            DataSerializers.STRING);
    public static final String FLOWERPOT_CONTENTS_TAG = "flowerpotContents";
    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(BlopoleEntity.class, DataSerializers.BOOLEAN);
    public static final String FROM_BUCKET_TAG = "fromBucket";
    private static final String TIME_UNTIL_BURP_TAG = "timeUntilNextBurp";


    public BlopoleEntity(EntityType<? extends TameableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 7.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                .createMutableAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.0D);
    }

    public static boolean canAnimalSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return true;
        //return (worldIn.getBlockState(pos.down()).isSolid() || worldIn.getBlockState(pos).isIn(Blocks.WATER));
    }

    @Nonnull
    protected PathNavigator createNavigator(@Nonnull World world) {
        return new WalkAndSwimNavigator(this, world);
    }

    @Override
    public int getMaxAir() {
        return 1200;
    }

    @Override
    @Nonnull
    // Called on right click by player
    public ActionResultType func_230254_b_(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldItemStack = player.getHeldItem(hand);
        Item heldItem = heldItemStack.getItem();
        if (heldItem == Items.BUCKET && this.isAlive()) {
            if (!world.isRemote) {
                // TODO: Maybe there is a way to make this client only?
                StringTextComponent info = new StringTextComponent("The Blopole doesn't like the dry bucket.");
                SChatPacket schatpacket = new SChatPacket(info, ChatType.GAME_INFO, Util.DUMMY_UUID);
                ((ServerPlayerEntity) player).connection.sendPacket(schatpacket);
            }
            return ActionResultType.FAIL;
        }
        if (heldItem == Items.WATER_BUCKET && this.isAlive()) {
            return handleBucketing(player, hand);
        }
        if (isTamed()) {
            ActionResultType result = handleFlowerpotting(player, heldItemStack);
            if (result != null) return result;
            // Sitting
            ActionResultType actionresulttype = super.func_230254_b_(player, hand);
            if (!actionresulttype.isSuccessOrConsume() && !isChild() && isOwner(player) && !world.isRemote && hand == Hand.MAIN_HAND) {
                this.func_233687_w_(!this.isSitting());
            }
            return actionresulttype;
        } else {
            handleTaming(player, heldItemStack);
        }

        // TODO: Revise/refactor this
        return super.func_230254_b_(player, hand);
    }

    private void handleTaming(PlayerEntity player, ItemStack heldItemStack) {
        if (world.isRemote) {
            return;
        }
        if (heldItemStack.getItem() == Items.SLIME_BALL) {
            if (!player.abilities.isCreativeMode) {
                heldItemStack.shrink(1);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                ((ServerWorld) world).spawnParticle(ParticleTypes.ITEM_SLIME, this.getPosX(), this.getPosY(), this.getPosZ(), 10, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, 1.0D);
            }
            if (this.rand.nextInt(2) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                this.setTamedBy(player);
                this.navigator.clearPath();
                this.func_233687_w_(true);
                this.world.setEntityState(this, (byte) 7);
                this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_PLACE, 1.0f, 1.0f);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
        }
    }

    @Nonnull
    private ActionResultType handleBucketing(PlayerEntity player, @Nonnull Hand hand) {
        // TODO: Maybe this can be a static function, also used in Toad?
        ItemStack itemstack = player.getHeldItem(hand);
        this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
        itemstack.shrink(1);
        ItemStack creatureBucket = new ItemStack(Registration.BLOPOLE_BUCKET.get());
        CompoundNBT itemNBT = new CompoundNBT();
        itemNBT.put("EntityTag", serializeNBT());
        creatureBucket.setTag(itemNBT);

        if (!this.world.isRemote) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, creatureBucket);
        }

        if (itemstack.isEmpty()) {
            player.setHeldItem(hand, creatureBucket);
        } else if (!player.inventory.addItemStackToInventory(creatureBucket)) {
            player.dropItem(creatureBucket, false);
        }

        this.remove();
        return ActionResultType.func_233537_a_(this.world.isRemote);
    }

    private ActionResultType handleFlowerpotting(PlayerEntity player, ItemStack heldItemStack) {
        if (!hasFlowerpot()) {
            if (heldItemStack.getItem() == Items.FLOWER_POT) {
                setHasFlowerpot(true);
                consumeItemFromStack(player, heldItemStack);
                return ActionResultType.func_233537_a_(world.isRemote); // CONSUME if client
            }
        } else {
            // Blopole already has flowerpot on his head
            if (player.isSneaking()) {
                dropInventory();
                return ActionResultType.SUCCESS;
            } else {
                // Player is not sneaking -> Try to put flower into pot
                ResourceLocation registryName = heldItemStack.getItem().getRegistryName();
                Map<ResourceLocation, Supplier<? extends Block>> fullPots = FlowerPotHelper.getFullPots();

                String flowerpotContents = getFlowerpotContents();
                if (registryName != null && fullPots.containsKey(registryName)) {
                    // If there already is a flower, drop the existing one
                    if (!flowerpotContents.isEmpty()) {
                        Block flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(flowerpotContents));
                        entityDropItem(new ItemStack(flowerBlock));
                    }
                    setFlowerpotContents(registryName.toString());
                    consumeItemFromStack(player, heldItemStack);
                    return ActionResultType.func_233537_a_(world.isRemote);
                }
            }
        }
        return null;
    }


    @Override
    protected void dropInventory() {
        if (world.isRemote) {
            return;
        }
        String flowerpotContents = getFlowerpotContents();
        if (!flowerpotContents.isEmpty()) {
            Block flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(flowerpotContents));
            entityDropItem(new ItemStack(flowerBlock));
            setFlowerpotContents("");
        }
        if (hasFlowerpot()) {
            entityDropItem(Items.FLOWER_POT);
            setHasFlowerpot(false);
        }
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
            if (isInWater) {
                return IDLE_SWIM_ANIM;
            } else {
                switch (getChosenIdleAnim()) {
                    case 0:
                        return IDLE_ANIM;
                    case 1:
                        return IDLE_SIDE_ANIM;
                    case 2:
                        return IDLE_FLIPPED_ANIM;
                }
            }
        }

        return IDLE_ANIM;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.setAnimation(chooseAnimation());
        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new CreatureBreatheAirGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new CreatureTemptGoal(this, 1.00, BREEDING_ITEM));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new BlopoleLookRandomlyGoal(this));
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!world.isRemote && this.isAlive()) {
            if (!this.isChild() && --timeUntilNextBurp <= 0) {
                this.timeUntilNextBurp = this.rand.nextInt(6000) + 6000;
                this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, rand.nextFloat() / 2 + 1.0F);
                entityDropItem(Items.CLAY_BALL);
            }

            // Passive healing every 5 seconds when in water
            if (isInWater() && this.getHealth() < this.getMaxHealth() && ticksExisted % 5 * 20 == 0) {
                this.heal(1);
            }
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
        return Registration.BLOPOLE.get().create(p_241840_1_);
    }

    public boolean hasFlowerpot() {
        return dataManager.get(HAS_FLOWERPOT);
    }

    public void setHasFlowerpot(boolean hasFlowerpot) {
        dataManager.set(HAS_FLOWERPOT, hasFlowerpot);
    }

    public String getFlowerpotContents() {
        return dataManager.get(FLOWERPOT_CONTENTS);
    }

    public void setFlowerpotContents(String flowerpotContents) {
        dataManager.set(FLOWERPOT_CONTENTS, flowerpotContents);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(HAS_FLOWERPOT, false);
        this.dataManager.register(FLOWERPOT_CONTENTS, "");
        this.dataManager.register(FROM_BUCKET, false);
        this.dataManager.register(CHOSEN_IDLE_ANIM, (byte) 0);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        setHasFlowerpot(compound.getBoolean(HAS_FLOWERPOT_TAG));
        setFlowerpotContents(compound.getString(FLOWERPOT_CONTENTS_TAG));
        setFromBucket(compound.getBoolean(FROM_BUCKET_TAG));
        timeUntilNextBurp = compound.getInt(TIME_UNTIL_BURP_TAG);
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean(HAS_FLOWERPOT_TAG, hasFlowerpot());
        compound.putString(FLOWERPOT_CONTENTS_TAG, getFlowerpotContents());
        compound.putBoolean(FROM_BUCKET_TAG, isFromBucket());
        compound.putInt(TIME_UNTIL_BURP_TAG, timeUntilNextBurp);
    }

    public byte getChosenIdleAnim() {
        return dataManager.get(CHOSEN_IDLE_ANIM);
    }

    public void setChosenIdleAnim(byte chosenAnim) {
        dataManager.set(CHOSEN_IDLE_ANIM, chosenAnim);
    }

    @Override
    public boolean isFromBucket() {
        return dataManager.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean isFromBucket) {
        dataManager.set(FROM_BUCKET, isFromBucket);
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isFromBucket() || this.isTamed();
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isFromBucket() && !this.hasCustomName() && !this.isTamed();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return Registration.BLOPOLE_AMBIENT.get();
    }
}
