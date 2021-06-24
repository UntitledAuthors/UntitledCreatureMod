package net.untitledcreaturemod.creature.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;

public class CreatureFleeGoal<T extends PathAwareEntity & FleeingCreature> extends Goal {

    private final T creature;
    private final EntityNavigation navigation;
    private final double fleeSpeed;

    public CreatureFleeGoal(T creature, double fleeSpeed) {
        this.creature = creature;
        this.navigation = creature.getNavigation();
        this.fleeSpeed = fleeSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return false;
    }
}
