package com.untitledauthors.untitledcreaturemod.items.tools;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class StonehornDaggerItem extends SwordItem {



    public StonehornDaggerItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }


    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return repair.getItem() == Registration.ANTELOPE_HORN.get();
    }

//    @OnlyIn(Dist.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
//    {
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//        tooltip.add((new TranslationTextComponent("item.easy_steel.steel.line1").mergeStyle(TextFormatting.GREEN)));
//    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        System.out.println("ITEM UPDATING!");
        return false;
    }
}
