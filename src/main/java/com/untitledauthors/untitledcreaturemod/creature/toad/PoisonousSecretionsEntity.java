package com.untitledauthors.untitledcreaturemod.creature.toad;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class PoisonousSecretionsEntity extends ProjectileItemEntity {
    public PoisonousSecretionsEntity(EntityType<? extends PoisonousSecretionsEntity> entityType,
                                     World world) {
        super(entityType, world);
    }

    public PoisonousSecretionsEntity(World worldIn, PlayerEntity playerIn) {
        super(Registration.POISONOUS_SECRETIONS_PROJECTILE.get(), playerIn, worldIn);
    }

    public PoisonousSecretionsEntity(World worldIn, double x, double y, double z) {
        super(Registration.POISONOUS_SECRETIONS_PROJECTILE.get(), x, y, z, worldIn);
    }

    public static final int PROJECTILE_DAMAGE = 1;
    public static final int POISON_DURATION_S = 10;

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return Registration.POISONOUS_SECRETIONS_ITEM.get();
    }

    protected void onEntityHit(EntityRayTraceResult rayTraceResult) {
        super.onEntityHit(rayTraceResult);
        Entity hitEntity = rayTraceResult.getEntity();
        hitEntity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()),
                PROJECTILE_DAMAGE);
        if (hitEntity instanceof LivingEntity) {
            ((LivingEntity)hitEntity).addPotionEffect(new EffectInstance(Effects.POISON, POISON_DURATION_S * 20, 0));
        }
    }

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            if (result.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockTrace = (BlockRayTraceResult) result;
                BlockPos placePos = blockTrace.getPos().offset(blockTrace.getFace());
                if (!world.getBlockState(placePos).isSolid() && world.getBlockState(placePos.down()).isSolid()) {
                    world.playSound(null, placePos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 0.8f, 1.2f);
                    world.setBlockState(placePos, Registration.POISONOUS_SECRETIONS_CARPET.get().getDefaultState());
                }
            }
        }
        this.remove();
    }

}
