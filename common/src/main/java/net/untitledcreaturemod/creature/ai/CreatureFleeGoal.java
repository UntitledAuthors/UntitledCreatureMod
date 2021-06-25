package net.untitledcreaturemod.creature.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class CreatureFleeGoal<T extends PathAwareEntity & FleeingCreature> extends Goal {

    private final T creature;
    private final EntityNavigation navigation;
    private LivingEntity avoidTarget;
    private final double fleeSpeed;
    private Path path;

    public CreatureFleeGoal(T creature, double fleeSpeed) {
        this.creature = creature;
        this.navigation = creature.getNavigation();
        this.fleeSpeed = fleeSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        this.avoidTarget = creature.getAttackingEntity();
        if (this.avoidTarget == null || !creature.shouldFlee() || !this.avoidTarget.isAlive()) {
            return false;
        }
        Vec3d target = null;
        if (creature instanceof DirectedFleeingCreature) {
            DirectedFleeingCreature herdCreature = (DirectedFleeingCreature) creature;
            target = herdCreature.getCommonFleeTarget();
            if (target != null && target.squaredDistanceTo(creature.getPos()) <= 20) {
                Vec3d commonFleeTarget = null;
                for (int i = 0; i < 20; i++) {
                    commonFleeTarget = NoPenaltyTargeting.find(creature, 32, 7, avoidTarget.getPos());
                    if (commonFleeTarget != null) {
                        break;
                    }
                }
                herdCreature.setCommonFleeTarget(commonFleeTarget);
                herdCreature.alertOthersToFlee(avoidTarget, commonFleeTarget);
            }
        }

        if (target == null) {
            target = NoPenaltyTargeting.find(this.creature, 16, 7, avoidTarget.getPos());
        }

        if (target == null) {
            return false;
        } else if (this.avoidTarget.squaredDistanceTo(target.x, target.y, target.z) < this.avoidTarget.squaredDistanceTo(this.creature)) {
            return false;
        } else {
            this.path = this.navigation.findPathTo(target.x, target.y, target.z, 0);
            return this.path != null;
        }
    }

    @Override
    public boolean shouldContinue() {
        return navigation.isFollowingPath();
    }

    @Override
    public void start() {
        this.navigation.startMovingAlong(path, this.fleeSpeed);
    }

    @Override
    public void stop() {
        this.avoidTarget = null;
    }

    public void tick() {
        if (creature.shouldJumpWhileFleeing() && creature.squaredDistanceTo(this.avoidTarget) < 49.0D) {
            if (creature.getRandom().nextFloat() < 0.2F) {
                creature.getJumpControl().setActive();
            }
        }
    }
}
