package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class StartJoustGoal extends Goal {
    private final RockAntelopeEntity antelope;
    private final double moveSpeed;
    private final World world;
    private RockAntelopeEntity targetJouster;
    private boolean startedJoust = false;

    public StartJoustGoal(RockAntelopeEntity entity, double moveSpeed) {
        this.antelope = entity;
        this.moveSpeed = moveSpeed;
        this.world = entity.getEntityWorld();
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    // TODO: Lower to 1000 or something
    // 1 in CHANCE is the probability of an antelope wanting to headbutt another one
    private static final int START_HEADBUTT_CHANCE = 100;

    @Override
    public boolean shouldExecute() {
        if (antelope.isChild()) {
            return false;
        }
        if (antelope.getRNG().nextInt(500) == 0) {
            this.targetJouster = (RockAntelopeEntity) findNearestHeadbutter();
            return this.targetJouster != null;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetJouster.isAlive() && !this.startedJoust;
    }

    public void resetTask() {
        this.targetJouster = null;
        this.startedJoust = false;
        antelope.getNavigator().clearPath();
    }


    public void tick() {
        antelope.getLookController().setLookPositionWithEntity(targetJouster, 90.0F, (float) antelope.getVerticalFaceSpeed());
        antelope.getNavigator().tryMoveToEntityLiving(targetJouster, moveSpeed);
        if (antelope.getDistanceSq(this.targetJouster) < 15.0D) {
            // TODO: Set position to the optimal one for the animation?
            this.headbut();
            this.startedJoust = true;
        }
    }

    private void headbut() {
        // This should trigger/start the JoustGoal
        this.antelope.setJoustingPartner(targetJouster.getEntityId());
        this.targetJouster.setJoustingPartner(antelope.getEntityId());
    }

    private static double JOUST_MAX_DISTANCE = 10.0D;
    private static final EntityPredicate nearbyPredicate = (new EntityPredicate()).setDistance(JOUST_MAX_DISTANCE).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired();
    @Nullable
    private RockAntelopeEntity findNearestHeadbutter() {
        List<RockAntelopeEntity> list = world.getTargettableEntitiesWithinAABB(RockAntelopeEntity.class,
                nearbyPredicate, antelope, antelope.getBoundingBox().grow(JOUST_MAX_DISTANCE));
        double min_distance = Double.MAX_VALUE;
        RockAntelopeEntity foundPartner = null;

        for(RockAntelopeEntity potentialPartner : list) {
            if (antelope.getDistanceSq(potentialPartner) < min_distance) {
                // TODO: Maybe only joust with partners that have enough horns
                if (potentialPartner.isChild() || potentialPartner.getJoustingPartner() != 0) {
                    continue;
                }
                foundPartner = potentialPartner;
                min_distance = antelope.getDistanceSq(potentialPartner);
            }
        }

        return foundPartner;
    }
}
