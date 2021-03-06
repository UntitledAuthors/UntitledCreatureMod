package com.untitledauthors.untitledcreaturemod.items;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class LifeVestTickHandler {
    private static boolean wasUsingVest = false;
    public static final double FLOAT_ACCELERATION = 0.12; // Bubble column is 0.06
    public static final double FLOAT_MAX_SPEED = 0.9; // Bubble column is 0.7
    @SubscribeEvent
    public static void postPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        PlayerEntity player = event.player;
        ItemStack equippedArmor = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (equippedArmor.isEmpty() || equippedArmor.getItem() != Registration.LIFE_VEST.get()) {
            return;
        }

        if (player.isInWaterOrBubbleColumn()) {
            // Float/accelerate player upwards
            Vector3d playerVelocity = player.getMotion();
            player.setMotion(playerVelocity.x, Math.min(FLOAT_MAX_SPEED, playerVelocity.y + FLOAT_ACCELERATION), playerVelocity.z);
            player.fallDistance = 0.0F;
            // TODO: Cancel out magma/downwards bubble columns?

            if (!wasUsingVest) {
                player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
                wasUsingVest = true;
            }
            // TODO: Probably have to make the particles serverside/communicated since other player probably can't see them
            if (event.side.isClient()) {
                World world = event.player.getEntityWorld();
                Random rand = world.getRandom();
                Vector3d pos = player.getPositionVec().subtract(0.5, 0.5f, 0.5f);
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();
                world.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, 0.0D);
                world.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + (double)rand.nextFloat(), y + (double)rand.nextFloat(), z + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D);
                if (rand.nextInt(200) == 0) {
                    world.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
                }
            }
        } else {
            wasUsingVest = false;
        }
    }
}
