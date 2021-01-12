package com.untitledauthors.untitledcreaturemod.items.tools;



//   Vanilla Material Reference:

//HARVEST LEVEL | MAX USES | ATTACK DAMAGE | EFFICIENCY | ENCHANTABILITY
//    GOLD(0, 32, 12.0F, 0.0F, 22)
//    WOOD(0, 59, 2.0F, 0.0F, 15)
//    STONE(1, 131, 4.0F, 1.0F, 5)
//    IRON(2, 250, 6.0F, 2.0F, 14)
//    DIAMOND(3, 1561, 8.0F, 3.0F, 10)


import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum CustomTierList implements IItemTier
{

    STONEHORN(3.0f, 1.0f, 100, 1, 7, () -> {
        return Ingredient.fromItems(Registration.ANTELOPE_HORN.get());
    });
    private float attackDamage, efficiency;
    private int durability, harvestLevel, enchantability;
    final LazyValue<Ingredient> repairMaterial;



    private CustomTierList(float attackDamage, float efficiency, int durability, int harvestLevel, int enchantability, Supplier<Ingredient> repairMaterial)
    {
        this.attackDamage = attackDamage;
        this.efficiency = efficiency;
        this.durability = durability;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.enchantability = enchantability;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    @Override
    public int getMaxUses() {
        return this.durability;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }
}
