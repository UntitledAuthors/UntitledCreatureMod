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
        this.avoidTarget = creature.getFleeTarget();
        if (this.avoidTarget == null || !creature.shouldFlee() || !this.avoidTarget.isAlive()) {
            return false;
        }
        Vector3d randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 7, this.avoidTarget.getPositionVec());
        if (randomTarget == null) {
            return false;
        } else if (this.avoidTarget.getDistanceSq(randomTarget.x, randomTarget.y, randomTarget.z) < this.avoidTarget.getDistanceSq(this.creature)) {
            return false;
        } else {
            this.path = this.navigator.getPathToPos(randomTarget.x, randomTarget.y, randomTarget.z, 0);
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
        if (creature.getDistanceSq(this.avoidTarget) < 49.0D) {
            if (creature.getRNG().nextFloat() < 0.2F) {
                creature.getJumpController().setJumping();
            }
        }
    }
}
