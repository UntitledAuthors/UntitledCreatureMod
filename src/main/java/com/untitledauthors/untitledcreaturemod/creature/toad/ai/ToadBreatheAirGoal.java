package com.untitledauthors.untitledcreaturemod.creature.toad.ai;


import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class ToadBreatheAirGoal extends Goal {
    private final CreatureEntity creature;
    private final int maxAir;

    public ToadBreatheAirGoal(CreatureEntity creature) {
        this.creature = creature;
        this.maxAir = creature.getMaxAir();
        this.setMutexFlags(EnumSet.of(Flag.JUMP));
    }

    public boolean shouldExecute() {
        return this.creature.getAir() < 100;
    }

    public boolean shouldContinueExecuting() {
        return this.creature.getAir() < maxAir - 40;
    }

    public void tick() {
        if (creature.isInWater()) {
            creature.moveVertical = 1.0f;
            creature.moveRelative(0.02F, new Vector3d(this.creature.moveStrafing, this.creature.moveVertical, this.creature.moveForward));
        } else {
            creature.moveVertical = 0.0f;
        }
    }

    @Override
    public void resetTask() {
        creature.moveVertical = 0.0f;
    }
}

