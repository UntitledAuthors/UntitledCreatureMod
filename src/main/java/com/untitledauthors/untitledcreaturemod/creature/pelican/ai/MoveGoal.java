package com.untitledauthors.untitledcreaturemod.creature.pelican.ai;

import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

abstract class MoveGoal extends Goal {
    public MoveGoal() {
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }
}


