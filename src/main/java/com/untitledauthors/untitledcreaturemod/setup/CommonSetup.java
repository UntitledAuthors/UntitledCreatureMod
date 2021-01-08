package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import com.untitledauthors.untitledcreaturemod.items.ToadMobEggItem;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;


public class CommonSetup {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("untitledcreaturemod") {
        @Override
        public ItemStack createIcon() {
            //FIX THIS USING REGISTRY INSTEAD OF MESSY FIX
            return new ItemStack(ToadMobEggItem::new);
        }
    };


    public static void setupCreatures() {
        GlobalEntityTypeAttributes.put(Registration.TOAD.get(), ToadEntity.getDefaultAttributes().create());
    }
}
