package com.untitledauthors.untitledcreaturemod.creature.toad;

import net.minecraft.entity.EntityType;
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
import java.util.function.Supplier;

// Most of the code was inspired by SlimeInABucket from Quark(CC BY SA):
// https://github.com/Vazkii/Quark/blob/4e0031f5e044d121a8a638e3a7f58fb36f3f4c5f/src/main/java/vazkii/quark/content/tools/item/SlimeInABucketItem.java
public class ToadBucket extends Item {
    private final Supplier<? extends EntityType<? extends AnimalEntity>> toadTypeSupplier;

    public ToadBucket(Supplier<? extends EntityType<? extends AnimalEntity>> toadType, Properties builder) {
        super(builder);
        this.toadTypeSupplier = toadType;
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        Direction facing = context.getFace();

        if(!world.isRemote) {
            // If executed on server
            placeToad((ServerWorld) world, pos.offset(facing), context.getItem().getTag());
            if (player != null) {
                player.swingArm(hand);
                if(!player.isCreative()) {
                    player.setHeldItem(hand, new ItemStack(Items.BUCKET));
                }
            }
        }
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        return ActionResultType.SUCCESS;
    }

    private void placeToad(ServerWorld worldIn, BlockPos pos, CompoundNBT entityData) {
       ToadEntity toad = new ToadEntity(toadTypeSupplier.get(), worldIn);
        toad.setFromBucket(true);
        if (entityData != null) {
            toad.read(entityData);
        }
        toad.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        worldIn.addEntity(toad);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT toad_data = stack.getTag();
            if (toad_data != null && toad_data.contains("CustomName")) {
                ITextComponent custom = ITextComponent.Serializer.getComponentFromJson(toad_data.getString("CustomName"));
                return new TranslationTextComponent("item.untitledcreaturemod.toad_bucket.named", custom);
            }
        }
        return super.getDisplayName(stack);
    }
}
