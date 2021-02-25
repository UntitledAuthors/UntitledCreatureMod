package com.untitledauthors.untitledcreaturemod.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class LifeVestItem extends ArmorItem {
    public LifeVestItem(Properties properties) {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        EquipmentSlotType chestPlateSlot = MobEntity.getSlotForItemStack(heldItem);

        ItemStack equippedArmor = player.getItemStackFromSlot(chestPlateSlot);
        player.setItemStackToSlot(chestPlateSlot, heldItem.copy());
        if (!equippedArmor.isEmpty()) {
            player.setHeldItem(hand, equippedArmor);
            return ActionResult.resultSuccess(heldItem);
        } else {
            heldItem.setCount(0);
            return ActionResult.func_233538_a_(heldItem, world.isRemote());
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        // TODO: Could do logic in here probably. Not sure if particles can also be spawned in here though
        //if (player.isInWaterOrBubbleColumn()) {
        //    player.onEnterBubbleColumn(false);
        //    Vector3d motion = player.getMotion();
        //    player.onEnterBubbleColumn(false);
        //    //player.setMotion(motion.getX(),10000, motion.getZ());
        //}
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".desc"));
    }
}
