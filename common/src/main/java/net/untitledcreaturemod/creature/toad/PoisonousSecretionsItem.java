package net.untitledcreaturemod.creature.toad;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PoisonousSecretionsItem extends Item {
    public PoisonousSecretionsItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
        if (!world.isClient) {
            PoisonousSecretionsEntity projectile = new PoisonousSecretionsEntity(world, player);
            projectile.setItem(itemstack);
            // Shoot projectile
            projectile.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(projectile);
        }
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!player.isCreative()) {
            itemstack.decrement(1);
        }
        return TypedActionResult.success(itemstack, world.isClient);
    }
}
