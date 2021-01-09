package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;

// Mostly copied from TurtleEntity.PlayerTemptGoal
public class ToadTemptGoal extends Goal {
    private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(10.0D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
    private final ToadEntity turtle;
    private final double speed;
    private PlayerEntity tempter;
    private int cooldown;
    private final Set<Item> temptItems;

    ToadTemptGoal(ToadEntity toad, double speedIn, Item temptItem) {
        this.turtle = toad;
        this.speed = speedIn;
        this.temptItems = Sets.newHashSet(temptItem);
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        } else {
            this.tempter = this.turtle.world.getClosestPlayer(ENTITY_PREDICATE, this.turtle);
            if (this.tempter == null) {
                return false;
            } else {
                return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
            }
        }
    }

    private boolean isTemptedBy(ItemStack p_203131_1_) {
        return this.temptItems.contains(p_203131_1_.getItem());
    }

    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    public void resetTask() {
        this.tempter = null;
        this.turtle.getNavigator().clearPath();
        this.cooldown = 100;
    }

    public void tick() {
        this.turtle.getLookController().setLookPositionWithEntity(this.tempter, (float)(this.turtle.getHorizontalFaceSpeed() + 20), (float)this.turtle.getVerticalFaceSpeed());
        if (this.turtle.getDistanceSq(this.tempter) < 6.25D) {
            this.turtle.getNavigator().clearPath();
        } else {
            this.turtle.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
        }

    }
}

