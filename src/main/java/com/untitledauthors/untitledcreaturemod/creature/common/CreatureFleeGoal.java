package com.untitledauthors.untitledcreaturemod.creature.common;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class CreatureFleeGoal<T extends CreatureEntity & FleeingCreature> extends Goal {
    private final T creature;
    private final PathNavigator navigator;
    private final double fleeSpeed;
    private LivingEntity avoidTarget;
    private Path path;

    public CreatureFleeGoal(T creature, double fleeSpeed) {
        this.creature = creature;
        this.navigator = creature.getNavigator();
        this.fleeSpeed = fleeSpeed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        this.avoidTarget = creature.getAttackingEntity();
        if (this.avoidTarget == null || !creature.shouldFlee() || !this.avoidTarget.isAlive()) {
            return false;
        }
        Vector3d target = null;
        if (creature instanceof DirectedFleeingCreature) {
            DirectedFleeingCreature herdCreature = (DirectedFleeingCreature)creature;
            target = herdCreature.getCommonFleeTarget();
            if (target != null && target.squareDistanceTo(creature.getPositionVec()) <= 20) {
                Vector3d commonFleeTarget = null;
                for (int i = 0; i < 20; i++) {
                    commonFleeTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(creature, 32, 7, avoidTarget.getPositionVec());
                    if (commonFleeTarget != null) {
                        break;
                    }
                }
                herdCreature.setCommonFleeTarget(commonFleeTarget);
                herdCreature.alertOthersToFlee(avoidTarget, commonFleeTarget);
            }
        }

        if (target == null) {
            target = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 7, this.avoidTarget.getPositionVec());
        }

        // TODO: Understand this again
        if (target == null) {
            return false;
        } else if (this.avoidTarget.getDistanceSq(target.x, target.y, target.z) < this.avoidTarget.getDistanceSq(this.creature)) {
            return false;
        } else {
            this.path = this.navigator.getPathToPos(target.x, target.y, target.z, 0);
            return this.path != null;
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.navigator.noPath();
    }

    public void startExecuting() {
        this.navigator.setPath(this.path, this.fleeSpeed);
    }

    public void resetTask() {
        this.avoidTarget = null;
    }

    public void tick() {
        if (creature.shouldJumpWhileFleeing() && creature.getDistanceSq(this.avoidTarget) < 49.0D) {
            if (creature.getRNG().nextFloat() < 0.2F) {
                creature.getJumpController().setJumping();
            }
        }
    }
}
