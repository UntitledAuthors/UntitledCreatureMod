package com.untitledauthors.untitledcreaturemod.creature.toad.ai;

import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class ToadFleeGoal extends Goal {
    private final ToadEntity toad;
    private final PathNavigator navigator;
    private final double fleeSpeed;
    private LivingEntity avoidTarget;
    private Path path;

    public ToadFleeGoal(ToadEntity toadEntity, double fleeSpeed) {
        this.toad = toadEntity;
        this.navigator = toadEntity.getNavigator();
        this.fleeSpeed = fleeSpeed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        this.avoidTarget = toad.getFleeTarget();
        if (this.avoidTarget == null) {
            return false;
        }
        Vector3d randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.toad, 16, 7, this.avoidTarget.getPositionVec());
        if (randomTarget == null) {
            return false;
        } else if (this.avoidTarget.getDistanceSq(randomTarget.x, randomTarget.y, randomTarget.z) < this.avoidTarget.getDistanceSq(this.toad)) {
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
        if (toad.getDistanceSq(this.avoidTarget) < 49.0D) {
            if (toad.getRNG().nextFloat() < 0.2F) {
                toad.getJumpController().setJumping();
            }
        }
    }
}
