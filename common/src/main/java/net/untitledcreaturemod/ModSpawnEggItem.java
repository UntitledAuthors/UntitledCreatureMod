package net.untitledcreaturemod;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.untitledcreaturemod.mixin.SpawnEggAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// This code is mostly taken and adapted from here
// https://github.com/Cadiboo/Example-Mod/blob/1.15.2/src/main/java/io/github/cadiboo/examplemod/item/ModdedSpawnEggItem.java
// Hope that forge will fix this soon
public class ModSpawnEggItem extends SpawnEggItem {
    public static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Supplier<EntityType<?>> entityTypeSupplier;

    public ModSpawnEggItem(Supplier<EntityType<?>> entityTypeSupplier, int primaryColor, int secondaryColor, Settings settings) {
        // TODO: They should be added on fabric, but how to do it in common? Mixin in forge?
        super(null, primaryColor, secondaryColor, settings);
        this.entityTypeSupplier = entityTypeSupplier;
        UNADDED_EGGS.add(this);
    }

    public static void addModdedEggs() {
        final Map<EntityType<?>, SpawnEggItem> EGGS = SpawnEggAccessor.getSpawnEggs();
        ItemDispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getEntityType(stack.getTag());
                entityType.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
                stack.decrement(1);
                return stack;
            }
        };
        for (final SpawnEggItem egg : ModSpawnEggItem.UNADDED_EGGS) {
            EGGS.put(egg.getEntityType(null), egg);
            DispenserBlock.registerBehavior(egg, dispenserBehavior);
        }
        ModSpawnEggItem.UNADDED_EGGS.clear();
    }

    @Override
    public EntityType<?> getEntityType(@Nullable CompoundTag arg) {
        return entityTypeSupplier.get();
    }
}
