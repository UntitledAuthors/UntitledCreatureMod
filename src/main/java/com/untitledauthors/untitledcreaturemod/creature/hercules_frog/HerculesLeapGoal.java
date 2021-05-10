package com.untitledauthors.untitledcreaturemod.creature.hercules_frog;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

public class HerculesLeapGoal extends Goal {
    private static final int LEAP_ROLLS = 20;
    private static final int BOUNCE_ANIM_LENGTH = 20 * 6;
    private final MobEntity leaper;
    private final float leapMotionY;
    private LivingEntity leapTarget;
    private int last_jump;

    public HerculesLeapGoal(MobEntity leapingEntity, float leapMotionYIn) {
        this.leaper = leapingEntity;
        this.leapMotionY = leapMotionYIn;
        this.last_jump = leaper.ticksExisted;
    }

    public boolean shouldExecute() {
        // Respect timeout
        if (last_jump + BOUNCE_ANIM_LENGTH > leaper.ticksExisted) {
            return false;
        }
        this.leapTarget = this.leaper.getAttackTarget();
        if (this.leapTarget == null) {
            return false;
        }

        double targetDistance = this.leaper.getDistanceSq(this.leapTarget);
        if (targetDistance >= 4.0D && targetDistance <= 32.0D) {
            if (!this.leaper.isOnGround()) {
                return false;
            } else {
                return this.leaper.getRNG().nextInt(LEAP_ROLLS) == 0;
            }
        } else {
            return false;
        }
    }

    public boolean shouldContinueExecuting() {
        return last_jump + BOUNCE_ANIM_LENGTH > leaper.ticksExisted;
    }

    @Override
    public void resetTask() {
    }

    public void startExecuting() {
        // TODO: Figure out collision
        Vector3d leaperMotion = this.leaper.getMotion();
        Vector3d targetPosition = new Vector3d(this.leapTarget.getPosX() - this.leaper.getPosX(), 0.0D, this.leapTarget.getPosZ() - this.leaper.getPosZ());
        if (targetPosition.lengthSquared() > 1.0E-7D) {
            targetPosition = targetPosition.normalize().scale(0.8D).add(leaperMotion.scale(0.2D));
        }

        last_jump = leaper.ticksExisted;
        this.leaper.setMotion(targetPosition.x, this.leapMotionY, targetPosition.z);
    }
}
