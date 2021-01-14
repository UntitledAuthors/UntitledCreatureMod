package com.untitledauthors.untitledcreaturemod.creature.toad.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

/// Attack target only once
public class AttackOnceGoal extends Goal {
    private CreatureEntity creature;
    private LivingEntity target;
    private double attackSpeedMultiplier;

    public AttackOnceGoal(CreatureEntity entity, double attackSpeedMultiplier) {
        this.creature = entity;
        this.attackSpeedMultiplier = attackSpeedMultiplier;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity livingentity = this.creature.getAttackTarget();
        if (livingentity == null) {
            return false;
        } else {
            this.target = livingentity;
            return true;
        }
    }

    public void resetTask() {
        this.target = null;
        this.creature.getNavigator().clearPath();
    }

    public void tick() {
        this.creature.getLookController().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
        double attackDistance = this.creature.getWidth() * 2.0F * this.creature.getWidth() * 2.0F;
        double targetDistance = this.creature.getDistanceSq(this.target.getPosX(), this.target.getPosY(), this.target.getPosZ());

        this.creature.getNavigator().tryMoveToEntityLiving(this.target, attackSpeedMultiplier);
        if (targetDistance < attackDistance) {
            this.creature.attackEntityAsMob(this.target);
            this.creature.setAttackTarget(null);
        }
    }
}
