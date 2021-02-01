package com.untitledauthors.untitledcreaturemod.creature.blopole;

import net.minecraft.entity.ai.goal.LookRandomlyGoal;

public class BlopoleLookRandomlyGoal extends LookRandomlyGoal {
    private final BlopoleEntity blopoleEntity;

    public BlopoleLookRandomlyGoal(BlopoleEntity blopoleEntity) {
        super(blopoleEntity);
        this.blopoleEntity = blopoleEntity;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        blopoleEntity.setChosenIdleAnim((byte)blopoleEntity.getRNG().nextInt(3));
    }
}
