package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class FollowLeaderGoal extends Goal {
    private final RockAntelopeEntity antelope;
    private final double moveSpeed;
    private static double LEADER_FOLLOW_DISTANCE = 50.0D;

    private RockAntelopeEntity leaderAntelope = null;
    private int delayCounter;

    public FollowLeaderGoal(RockAntelopeEntity antelope, double moveSpeed) {
        this.antelope = antelope;
        this.moveSpeed = moveSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (antelope.isLeader() || antelope.isChild()) {
            // Leaders shouldn't follow other leaders
            return false;
        }
        // Look for a leader antelope around
        if (leaderAntelope == null) {
            leaderAntelope = findNearestLeader();
        }
        if (leaderAntelope == null) {
            // Become a leader if there is none?
            antelope.setIsLeader(true);
            return false;
        }
        // Only follow them when the leader gets too far away
        return antelope.getDistanceSq(leaderAntelope) > LEADER_FOLLOW_DISTANCE;
    }

    private static final EntityPredicate nearbyPredicate = (new EntityPredicate()).setDistance(LEADER_FOLLOW_DISTANCE).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired();
    @Nullable
    private RockAntelopeEntity findNearestLeader() {
        List<RockAntelopeEntity> list = antelope.world.getTargettableEntitiesWithinAABB(RockAntelopeEntity.class,
                nearbyPredicate, antelope, antelope.getBoundingBox().grow(LEADER_FOLLOW_DISTANCE));
        double min_distance = Double.MAX_VALUE;
        RockAntelopeEntity foundLeader = null;

        for(RockAntelopeEntity potentialLeader : list) {
            if (antelope.getDistanceSq(potentialLeader) < min_distance) {
                foundLeader = potentialLeader;
                min_distance = antelope.getDistanceSq(potentialLeader);
            }
        }
        return foundLeader;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return antelope.getDistanceSq(leaderAntelope) > LEADER_FOLLOW_DISTANCE;
    }

    @Override
    public void startExecuting() {
        // Wait random amount of ticks between 10 and 20
        this.delayCounter = 10 + antelope.getRNG().nextInt(10);
    }

    @Override
    public void resetTask() {
        // NOTE: No they probably should not forget their leader
        // leaderAntelope = null;
    }

    public void tick() {
        if (--delayCounter <= 0) {
            delayCounter = 10;
            // TODO: Maybe move nearby a leader?
            antelope.getNavigator().tryMoveToEntityLiving(leaderAntelope, moveSpeed);
        }
    }
}
