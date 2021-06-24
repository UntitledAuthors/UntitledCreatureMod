package net.untitledcreaturemod.creature.ai;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class CreatureBreatheAirGoal extends Goal {
    private final PathAwareEntity creature;
    private final int maxAir;

    public CreatureBreatheAirGoal(PathAwareEntity creature) {
        this.creature = creature;
        this.maxAir = creature.getMaxAir();
        this.setControls(EnumSet.of(Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return this.creature.getAir() < 100;
    }

    @Override
    public boolean shouldContinue() {
        return this.creature.getAir() < maxAir - 40;
    }

    @Override
    public void stop() {
        creature.upwardSpeed = 0.0f;
    }

    @Override
    public void tick() {
        if (creature.isTouchingWater()) {
            creature.upwardSpeed = 1.0f;
            creature.updateVelocity(0.02F, new Vec3d(this.creature.sidewaysSpeed, this.creature.upwardSpeed, this.creature.forwardSpeed));
        } else {
            creature.upwardSpeed = 0.0f;
        }
    }
}
