package com.untitledauthors.untitledcreaturemod.items;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.potion.Effect;

public class AntelopeWarHorn extends Item {
    public AntelopeWarHorn(Properties properties) {
        super(properties);
    }

    // 20 Ticks = 1 second, cooldown duration and effect duration are in ticks
    public static int COOLDOWN_SEC = 15;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.getCooldownTracker().setCooldown(this, COOLDOWN_SEC * 20);
        worldIn.playSound(null, playerIn.getPosition(), Registration.WAR_HORN_SOUND.get(), SoundCategory.NEUTRAL, 2.0f, 1.0f);
        playerIn.addPotionEffect(new EffectInstance(Effect.get(5), 200, 0));
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
