package com.untitledauthors.untitledcreaturemod.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public class StonehornTier implements IItemTier {

    private int maxUses;
    private float efficiency;
    private float attackDamage;
    private int harvestLevel;
    private int enchantability;
//    private Ingredient repairMaterial = Ingredient.fromItems()

//    public StonehornTier(int maxUses, float efficiency, float attackDamage, int harvestLevel, int enchantability) {
//        this.maxUses = maxUses;
//        this.efficiency = efficiency;
//        this.attackDamage = attackDamage;
//        this.harvestLevel = harvestLevel;
//        this.enchantability = enchantability;
//    }

    @Override
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public float getEfficiency() {
        return efficiency;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getHarvestLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return null;
    }
}
