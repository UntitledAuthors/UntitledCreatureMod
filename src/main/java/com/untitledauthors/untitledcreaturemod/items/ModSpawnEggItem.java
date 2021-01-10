package com.untitledauthors.untitledcreaturemod.items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModSpawnEggItem extends SpawnEggItem {
    protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Supplier<? extends EntityType<?>> entityTypeSupplier;

    // This code is mostly taken and adapted from here
    // https://github.com/Cadiboo/Example-Mod/blob/1.15.2/src/main/java/io/github/cadiboo/examplemod/item/ModdedSpawnEggItem.java
    // Hope that forge will fix this soon
    public ModSpawnEggItem
    (
            //constructor args:
            final Supplier<? extends EntityType<?>> entityTypeSupplier, //A generic supplier of any generic that extends EntityType
            final int primaryColour, //primary colour to be used for the egg
            final int secondaryColour,  //secondary colour to be used for the egg
            Properties builder
    ) {
        //complete constructor
        super(null, primaryColour, secondaryColour, builder);
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier);
        UNADDED_EGGS.add(this);
    }

    public static void initUnaddedEggs() {
        final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior() {
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> entitytype = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
                entitytype.spawn(source.getWorld(), stack, null, source.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
                stack.shrink(1);
                return stack;
            }
        };

        for (final SpawnEggItem egg : UNADDED_EGGS) {
            EGGS.put(egg.getType(null), egg);
            DispenserBlock.registerDispenseBehavior(egg, defaultDispenseItemBehavior);
            // ItemColors for each spawn egg don't need to be registered because this method is called before ItemColors is created
        }
        UNADDED_EGGS.clear();
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT nbt) {
        return entityTypeSupplier.get();
    }
}
