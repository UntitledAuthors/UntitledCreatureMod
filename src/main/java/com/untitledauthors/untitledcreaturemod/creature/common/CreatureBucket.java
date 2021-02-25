package com.untitledauthors.untitledcreaturemod.creature.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Supplier;

// Most of the code was inspired by SlimeInABucket from Quark(CC BY SA):
// https://github.com/Vazkii/Quark/blob/4e0031f5e044d121a8a638e3a7f58fb36f3f4c5f/src/main/java/vazkii/quark/content/tools/item/SlimeInABucketItem.java
public class CreatureBucket extends Item {
    private final Supplier<? extends EntityType<? extends AnimalEntity>> entityTypeSupplier;
    private final Item bucketItem;

    public CreatureBucket(Supplier<? extends EntityType<? extends AnimalEntity>> entityType, Properties builder, Item bucketItem) {
        super(builder);
        this.entityTypeSupplier = entityType;
        this.bucketItem = bucketItem;
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(ItemUseContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getPos();
        final PlayerEntity player = context.getPlayer();
        final Hand hand = context.getHand();
        final Direction facing = context.getFace();

        if(!world.isRemote) {
            // If executed on server
            placeCreature((ServerWorld) world, pos.offset(facing), context.getItem().getTag());
            if (player != null) {
                player.swingArm(hand);
                if(!player.isCreative()) {
                    player.setHeldItem(hand, new ItemStack(bucketItem));
                }
            }
        }
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        return ActionResultType.SUCCESS;
    }

    private void placeCreature(ServerWorld worldIn, BlockPos pos, CompoundNBT entityData) {
        // Remove uuid when there already is a creature with same uuid.
        // This makes it possible to use the bucket in creative, cloning every tag except the uuid.
        if (entityData != null && entityData.contains("EntityTag") && entityData.getCompound("EntityTag").hasUniqueId("UUID")) {
            UUID uuid = entityData.getCompound("EntityTag").getUniqueId("UUID");
            if (worldIn.getEntityByUuid(uuid) != null) {
                entityData.getCompound("EntityTag").remove("UUID");
            }
        }
        Entity newCreature = entityTypeSupplier.get().create(worldIn, entityData, null, null, pos, SpawnReason.BUCKET, true, false);
        assert newCreature != null : "Bucket Creature is null? This should not happen :(";
        if (newCreature instanceof BucketCreature) {
            ((BucketCreature) newCreature).setFromBucket(true);
        }
        newCreature.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        worldIn.addEntity(newCreature);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT creature_data = stack.getTag();
            if (creature_data != null && creature_data.contains("EntityTag") && creature_data.getCompound("EntityTag").contains("CustomName")) {
                ITextComponent custom = ITextComponent.Serializer
                        .getComponentFromJson(creature_data.getCompound("EntityTag").getString("CustomName"));
                return new TranslationTextComponent("item.untitledcreaturemod.creature_bucket.named", custom);
            }
        }
        return super.getDisplayName(stack);
    }
}
