package com.untitledauthors.untitledcreaturemod.items;


import com.untitledauthors.untitledcreaturemod.setup.CommonSetup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UCMSpawnEggItem extends SpawnEggItem {

    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public UCMSpawnEggItem
            (
                    //constructor args:
                    final Supplier<? extends EntityType<?>> entityTypeSupplier, //A generic supplier of any generic that extends EntityType
                    int primaryColourIn, //primary colour to be used for the egg
                    int secondaryColourIn,  //secondary colour to be used for the egg
                    Properties builder
                    ) {
        //complete constructor
        super(null, primaryColourIn, secondaryColourIn, builder);
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier::get);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT nbt) {
        return entityTypeSupplier.get();
    }
}
