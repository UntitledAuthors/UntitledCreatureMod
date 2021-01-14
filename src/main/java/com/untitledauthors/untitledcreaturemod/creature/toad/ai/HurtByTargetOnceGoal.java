package com.untitledauthors.untitledcreaturemod.creature.toad.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;

public class HurtByTargetOnceGoal extends HurtByTargetGoal {
    public HurtByTargetOnceGoal(CreatureEntity creatureIn, Class<?>... excludeReinforcementTypes) {
        super(creatureIn, excludeReinforcementTypes);
    }

    @Override
    public boolean shouldContinueExecuting() {
        // Abort once AttackTarget gets reset by AttackOnceGoal
        return goalOwner.getAttackTarget() != null && super.shouldContinueExecuting();
    }
}
