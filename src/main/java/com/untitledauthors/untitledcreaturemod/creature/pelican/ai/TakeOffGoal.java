package com.untitledauthors.untitledcreaturemod.creature.pelican.ai;

import com.untitledauthors.untitledcreaturemod.creature.pelican.PelicanEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

public class TakeOffGoal extends Goal {
    private final PelicanEntity entity;

    public TakeOffGoal(PelicanEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return !entity.isFlying() && entity.getRNG().nextInt(200) == 0;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        entity.setFlying(true);
        entity.setMotion(new Vector3d(0, 5, 0));
    }
}
