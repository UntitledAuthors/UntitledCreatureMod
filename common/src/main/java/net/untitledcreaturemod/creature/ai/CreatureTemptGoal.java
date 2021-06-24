package net.untitledcreaturemod.creature.ai;

import com.google.common.collect.Sets;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;

// Mostly copied from TurtleEntity.ApproachFoodHoldingPlayerGoal
public class CreatureTemptGoal extends Goal {
    private static final TargetPredicate CLOSE_ENTITY_PREDICATE = (new TargetPredicate()).setBaseMaxDistance(10.0D).includeTeammates().includeInvulnerable();
    private final PathAwareEntity creature;
    private final double speed;
    private PlayerEntity targetPlayer;
    private int cooldown;
    private final Set<Item> attractiveItems;

    public CreatureTemptGoal(PathAwareEntity creature, double speed, Item attractiveItem) {
        this.creature = creature;
        this.speed = speed;
        this.attractiveItems = Sets.newHashSet(new Item[]{attractiveItem});
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        } else {
            this.targetPlayer = this.creature.world.getClosestPlayer(CLOSE_ENTITY_PREDICATE, this.creature);
            if (this.targetPlayer == null) {
                return false;
            } else {
                return this.isAttractive(this.targetPlayer.getMainHandStack()) || this.isAttractive(this.targetPlayer.getOffHandStack());
            }
        }
    }

    private boolean isAttractive(ItemStack stack) {
        return this.attractiveItems.contains(stack.getItem());
    }

    public boolean shouldContinue() {
        return this.canStart();
    }

    public void stop() {
        this.targetPlayer = null;
        this.creature.getNavigation().stop();
        this.cooldown = 100;
    }

    public void tick() {
        this.creature.getLookControl().lookAt(this.targetPlayer, (float)(this.creature.getBodyYawSpeed() + 20), (float)this.creature.getLookPitchSpeed());
        if (this.creature.squaredDistanceTo(this.targetPlayer) < 6.25D) {
            this.creature.getNavigation().stop();
        } else {
            this.creature.getNavigation().startMovingTo(this.targetPlayer, this.speed);
        }
    }
}