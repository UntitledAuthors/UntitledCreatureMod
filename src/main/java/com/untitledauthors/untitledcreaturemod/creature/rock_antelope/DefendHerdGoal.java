package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class DefendHerdGoal extends MeleeAttackGoal {
    RockAntelopeEntity antelope;
    public DefendHerdGoal(RockAntelopeEntity antelope, double speedIn, boolean useLongMemory) {
        super(antelope, speedIn, useLongMemory);
        this.antelope = antelope;
    }

    @Override
    public boolean shouldExecute() {
        if (!antelope.isLeader()) {
            return false;
        }
        boolean startAttacking = super.shouldExecute();
        // TODO: Check if this sets it to false too
        antelope.setIsAttacking(startAttacking);
        return startAttacking;
    }
}
