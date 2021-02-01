package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.untitledauthors.untitledcreaturemod.creature.BucketCreature;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.client.audio.Sound;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
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
import java.util.function.Supplier;

public class BlopoleEntity extends TameableEntity implements IAnimatable, BucketCreature
{
    private final AnimationFactory factory = new AnimationFactory(this);
    public static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle01");
    public static final AnimationBuilder IDLE_SIDE_ANIM = new AnimationBuilder().addAnimation("idle02");
    public static final AnimationBuilder IDLE_FLIPPED_ANIM = new AnimationBuilder().addAnimation("idle03");
    public static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static final Item BREEDING_ITEM = Items.SEA_PICKLE;

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


    public BlopoleEntity(EntityType<? extends TameableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 7.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    @Nonnull
    // Called on right click by player
    public ActionResultType func_230254_b_(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldItemStack = player.getHeldItem(hand);
        Item heldItem = heldItemStack.getItem();
        if (heldItem == Items.BUCKET && this.isAlive()) {
            return handleBucketing(player, hand);
        }
        if (isTamed()) {
            ActionResultType result = handleFlowerpotting(player, heldItemStack);
            if (result != null) return result;
        } else {
            handleTaming(player, heldItemStack);
        }

        // TODO: Revise/refactor this
        return super.func_230254_b_(player ,hand);
    }

    private void handleTaming(PlayerEntity player, ItemStack heldItemStack) {
        if (world.isRemote) {
            return;
        }
        if (heldItemStack.getItem() == Items.SLIME_BALL) {
            if (!player.abilities.isCreativeMode) {
                heldItemStack.shrink(1);
                world.playSound(null, getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
            if (this.rand.nextInt(2) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                this.setTamedBy(player);
                this.navigator.clearPath();
                this.func_233687_w_(true);
                this.world.setEntityState(this, (byte)7);
                world.playSound(null, getPosition(), SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            } else {
                this.world.setEntityState(this, (byte)6);
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
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, creatureBucket);
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
        entityDropItem(Items.FLOWER_POT);
        setHasFlowerpot(false);
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
        AnimationBuilder idleAnimation = IDLE_ANIM;
        switch (getChosenIdleAnim()) {
            case 0:
                idleAnimation = IDLE_ANIM;
                break;
            case 1:
                idleAnimation = IDLE_SIDE_ANIM;
                break;
            case 2:
                idleAnimation = IDLE_FLIPPED_ANIM;
                break;
        }
        AnimationBuilder anim = isMoving ? WALK_ANIM : idleAnimation;
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
        this.goalSelector.addGoal(8, new BlopoleLookRandomlyGoal(this));
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
        this.dataManager.register(CHOSEN_IDLE_ANIM, (byte)0);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        setHasFlowerpot(compound.getBoolean(HAS_FLOWERPOT_TAG));
        setFlowerpotContents(compound.getString(FLOWERPOT_CONTENTS_TAG));
        setFromBucket(compound.getBoolean(FROM_BUCKET_TAG));
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean(HAS_FLOWERPOT_TAG, hasFlowerpot());
        compound.putString(FLOWERPOT_CONTENTS_TAG, getFlowerpotContents());
        compound.putBoolean(FROM_BUCKET_TAG, isFromBucket());
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

    @Override
    public void playAmbientSound() {
        if (!this.isSilent()) {
            this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_BURP, this.getSoundCategory(), 1.0f,
                    getRNG().nextFloat()/2 + 1.0f);
        }
    }
}
