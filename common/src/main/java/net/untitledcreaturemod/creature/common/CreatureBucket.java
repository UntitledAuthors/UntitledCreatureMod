package net.untitledcreaturemod.creature.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Supplier;

public class CreatureBucket extends Item {
    private final Supplier<? extends EntityType<? extends AnimalEntity>> entityTypeSupplier;
    private final Item bucketItem;

    public CreatureBucket(Supplier<? extends EntityType<? extends AnimalEntity>> entityTypeSupplier, Settings settings, Item bucketItem) {
        super(settings);
        this.entityTypeSupplier = entityTypeSupplier;
        this.bucketItem = bucketItem;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos();
        final PlayerEntity player = context.getPlayer();
        final Direction blockSide = context.getSide();
        final Hand hand = context.getHand();

        if(!world.isClient) {
            placeCreature((ServerWorld) world, pos.offset(blockSide), context.getStack().getTag());
            if (player != null) {
                player.swingHand(hand);
                if(!player.isCreative()) {
                    player.setStackInHand(hand, new ItemStack(bucketItem));
                }
            }
        }
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        return ActionResult.SUCCESS;
    }

    private void placeCreature(ServerWorld world, BlockPos pos, CompoundTag entityData) {
        // Remove uuid when there already is a creature with same uuid.
        // This makes it possible to use the bucket in creative, cloning every tag except the uuid.
        if (entityData != null && entityData.contains("EntityTag") && entityData.getCompound("EntityTag").containsUuid("UUID")) {
            UUID uuid = entityData.getCompound("EntityTag").getUuid("UUID");
            if (world.getEntity(uuid) != null) {
                entityData.getCompound("EntityTag").remove("UUID");
            }
        }
        AnimalEntity newCreature = entityTypeSupplier.get().create(world, entityData, null, null, pos, SpawnReason.BUCKET, true, false);
        assert newCreature != null : "newDuck is null? This should not happen :(";
        if (newCreature instanceof BucketCreature) {
            ((BucketCreature) newCreature).setFromBucket(true);
        }
        newCreature.setPos(pos.getX(), pos.getY(), pos.getZ());

        newCreature.refreshPositionAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
        world.spawnEntity(newCreature);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag duckData = stack.getTag();
            if (duckData != null && duckData.contains("EntityTag") && duckData.getCompound("EntityTag").contains("CustomName")) {
                Text duckName = Text.Serializer.fromJson(duckData.getCompound("EntityTag").getString("CustomName"));
                return new TranslatableText("item.untitledduckmod.duck_sack.named", duckName);
            }
        }
        return super.getName(stack);
    }
}