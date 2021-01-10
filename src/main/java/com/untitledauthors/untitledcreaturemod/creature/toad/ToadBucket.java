package com.untitledauthors.untitledcreaturemod.creature.toad;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Supplier;

public class ToadBucket extends Item {
    private final Supplier<? extends EntityType<?>> toadTypeSupplier;

    public ToadBucket(Supplier<? extends EntityType<?>> toadType, Item.Properties builder) {
        super(builder);
        this.toadTypeSupplier = toadType;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        Direction facing = context.getFace();

        if(!world.isRemote) {
            // If executed on server
            // TODO: Save/Load NBT/Attributes from Toad
            placeToad((ServerWorld) world, context.getItem(), pos.offset(facing));
            player.swingArm(hand);
            if(!player.isCreative()) {
                player.setHeldItem(hand, new ItemStack(Items.BUCKET));
            }
        }
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        return ActionResultType.SUCCESS;
    }

    private void placeToad(ServerWorld worldIn, ItemStack stack, BlockPos pos) {
        Entity toad = this.toadTypeSupplier.get().spawn(worldIn, stack, null, pos, SpawnReason.BUCKET, true, false);
        // TODO: Make toad not despawn?
    }
}
