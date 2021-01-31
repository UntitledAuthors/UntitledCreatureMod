package com.untitledauthors.untitledcreaturemod.creature.blopole;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.ItemRegistry;
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

public class BlopoleEntity extends AnimalEntity implements IAnimatable
{
    private final AnimationFactory factory = new AnimationFactory(this);
    public static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle01");
    public static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    public static final Item BREEDING_ITEM = Items.SEA_PICKLE;
    public static DataParameter<Boolean> HAS_FLOWERPOT = EntityDataManager.createKey(BlopoleEntity.class,
            DataSerializers.BOOLEAN);
    public static final String HAS_FLOWERPOT_TAG = "hasFlowerpot";
    public static DataParameter<String> FLOWERPOT_CONTENTS = EntityDataManager.createKey(BlopoleEntity.class,
            DataSerializers.STRING);
    public static final String FLOWERPOT_CONTENTS_TAG = "flowerpotContents";


    public BlopoleEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
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
        return super.func_230254_b_(player ,hand);
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
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        setHasFlowerpot(compound.getBoolean(HAS_FLOWERPOT_TAG));
        setFlowerpotContents(compound.getString(FLOWERPOT_CONTENTS_TAG));

    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean(HAS_FLOWERPOT_TAG, hasFlowerpot());
        compound.putString(FLOWERPOT_CONTENTS_TAG, getFlowerpotContents());
    }

}
