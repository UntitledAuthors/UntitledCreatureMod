package net.untitledcreaturemod.creature.toad;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// TODO: Port
public class PoisonousSecretionsEntity extends ThrownItemEntity {
    public static final int PROJECTILE_DAMAGE = 1;
    public static final int POISON_DURATION_S = 10;

    public PoisonousSecretionsEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public PoisonousSecretionsEntity(EntityType<? extends ThrownItemEntity> entityType, double d, double e, double f, World world) {
        super(entityType, d, e, f, world);
    }

    public PoisonousSecretionsEntity(EntityType<? extends ThrownItemEntity> entityType, LivingEntity livingEntity, World world) {
        super(entityType, livingEntity, world);
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hitEntity = entityHitResult.getEntity();
        hitEntity.damage(DamageSource.thrownProjectile(this, this.getOwner()),
                PROJECTILE_DAMAGE);
        if (hitEntity instanceof LivingEntity) {
            ((LivingEntity)hitEntity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, POISON_DURATION_S * 20, 0));
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)3);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                BlockPos placePos = blockHit.getBlockPos().offset(blockHit.getSide());
                if (!world.getBlockState(placePos).isOpaque() && world.getBlockState(placePos.down()).isOpaque()) {
                    world.playSound(null, placePos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 0.8f, 1.2f);

                    // TODO: Add Carpet Block
                    //world.setBlockState(placePos, Registration.POISONOUS_SECRETIONS_CARPET.get().getDefaultState());
                }
            }
        }
        this.remove();

    }
}
