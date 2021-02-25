package com.untitledauthors.untitledcreaturemod.items;

import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties);
    }

    private static StringTextComponent rockAntelopeInfo(RockAntelopeEntity target) {
        return new StringTextComponent(String.format("ID: %d, leader: %s, child: %s",
                target.getEntityId(), target.isLeader(), target.isChild()));
    }

    private static StringTextComponent genericEntityInfo(LivingEntity target) {
        return new StringTextComponent(String.format("ID: %d", target.getEntityId()));
    }

    @Override
    @Nonnull
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn,
                                                     LivingEntity target, Hand hand) {
        World world = target.world;
        if (playerIn.isSneaking() && !world.isRemote) {
            if (target instanceof RockAntelopeEntity) {
                RockAntelopeEntity antelope = (RockAntelopeEntity) target;
                antelope.setRightHornPresent(true);
                antelope.setLeftHornPresent(true);
                // ((RockAntelopeEntity) target).setIsLeader(true);
                return ActionResultType.SUCCESS;
            }
        }
        if (!world.isRemote) {
            StringTextComponent info = genericEntityInfo(target);
            if (target instanceof RockAntelopeEntity) {
                info = rockAntelopeInfo((RockAntelopeEntity) target);
            }

            SChatPacket schatpacket = new SChatPacket(info, ChatType.GAME_INFO, Util.DUMMY_UUID);
            ((ServerPlayerEntity)playerIn).connection.sendPacket(schatpacket);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
