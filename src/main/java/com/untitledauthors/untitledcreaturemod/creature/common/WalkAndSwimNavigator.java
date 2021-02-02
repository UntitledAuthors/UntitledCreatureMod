package com.untitledauthors.untitledcreaturemod.creature.common;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class WalkAndSwimNavigator extends SwimmerPathNavigator {
    public WalkAndSwimNavigator(MobEntity creature, World world) {
        super(creature, world);
    }

    protected boolean canNavigate() {
        return true;
    }

    @Nonnull
    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new WalkAndSwimNodeProcessor();
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }
}
