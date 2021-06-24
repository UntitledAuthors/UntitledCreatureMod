package net.untitledcreaturemod;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// This code is mostly taken and adapted from here
// https://github.com/Cadiboo/Example-Mod/blob/1.15.2/src/main/java/io/github/cadiboo/examplemod/item/ModdedSpawnEggItem.java
// Hope that forge will fix this soon
public class ModSpawnEggItem extends SpawnEggItem {
    public static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Supplier<EntityType<?>> entityTypeSupplier;

    public ModSpawnEggItem(Supplier<EntityType<?>> entityTypeSupplier, int primaryColor, int secondaryColor, Settings settings) {
        super(null, primaryColor, secondaryColor, settings);
        this.entityTypeSupplier = entityTypeSupplier;
        UNADDED_EGGS.add(this);
    }

    @Override
    public EntityType<?> getEntityType(@Nullable CompoundTag arg) {
        return entityTypeSupplier.get();
    }
}
