package net.untitledcreaturemod.creature.toad;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PoisonousSecretionsCarpet extends Block {
    protected static final VoxelShape SHAPE = createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final int POISON_DURATION_S = 10;

    public PoisonousSecretionsCarpet(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 0.8f, 1.2f);
            ((LivingEntity)entityIn).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, POISON_DURATION_S * 20, 0));
            worldIn.removeBlock(pos, false);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    public static VoxelShape getSHAPE() {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return world.getBlockState(blockpos).isSideSolidFullSquare(world, blockpos, Direction.UP);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }
}
