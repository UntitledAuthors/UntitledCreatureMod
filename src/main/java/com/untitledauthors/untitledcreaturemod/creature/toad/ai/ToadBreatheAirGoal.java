package com.untitledauthors.untitledcreaturemod.creature.toad.ai;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;

public class ToadBreatheAirGoal extends Goal {
    private final CreatureEntity creature;
    private final int maxAir;

    public ToadBreatheAirGoal(CreatureEntity creature) {
        this.creature = creature;
        this.maxAir = creature.getMaxAir();
        this.setMutexFlags(EnumSet.of(Flag.JUMP));
        creature.getNavigator().setCanSwim(true);
    }

    public boolean shouldExecute() {
        return this.creature.getAir() < 100;
    }

    public boolean shouldContinueExecuting() {
        return this.creature.isInWater() && this.creature.getAir() < maxAir - 40;
    }

    public void startExecuting() {
        this.navigate();
    }

    private void navigate() {
        Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(MathHelper.floor(this.creature.getPosX() - 1.0D),
                MathHelper.floor(this.creature.getPosY()),
                MathHelper.floor(this.creature.getPosZ() - 1.0D),
                MathHelper.floor(this.creature.getPosX() + 1.0D),
                MathHelper.floor(this.creature.getPosY() + 8.0D),
                MathHelper.floor(this.creature.getPosZ() + 1.0D));
        BlockPos blockpos = null;

        for (BlockPos blockpos1 : iterable) {
            if (this.canBreatheAt(this.creature.world, blockpos1)) {
                blockpos = blockpos1;
                break;
            }
        }

        if (blockpos == null) {
            blockpos = new BlockPos(this.creature.getPosX(), this.creature.getPosY() + 8.0D, this.creature.getPosZ());
        }

        this.creature.getNavigator().tryMoveToXYZ(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ(), 1.0D);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.navigate();
        this.creature.moveVertical = 1.0f;
        this.creature.moveRelative(0.02F, new Vector3d(this.creature.moveStrafing, this.creature.moveVertical, this.creature.moveForward));
    }

    @Override
    public void resetTask() {
        this.creature.getNavigator().clearPath();
        this.creature.moveVertical = 0.0f;
    }

    private boolean canBreatheAt(IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        return (worldIn.getFluidState(pos).isEmpty() || blockstate.isIn(Blocks.BUBBLE_COLUMN)) && blockstate.allowsMovement(worldIn, pos, PathType.LAND);
    }
}

