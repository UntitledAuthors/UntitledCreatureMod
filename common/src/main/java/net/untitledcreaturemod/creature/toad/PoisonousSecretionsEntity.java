package net.untitledcreaturemod.creature.toad;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.untitledcreaturemod.architectury.networking.NetworkManager;


public class PoisonousSecretionsEntity extends ThrownItemEntity {
    public static final int PROJECTILE_DAMAGE = 1;
    public static final int POISON_DURATION_S = 10;

    public PoisonousSecretionsEntity(EntityType<? extends PoisonousSecretionsEntity> entityType,
                                     World world) {
        super(entityType, world);
    }

    public PoisonousSecretionsEntity(World worldIn, PlayerEntity playerIn) {
        super(Toad.POISONOUS_SECRETIONS_PROJECTILE.get(), playerIn, worldIn);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkManager.createAddEntityPacket(this);
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

                    world.setBlockState(placePos, Toad.POISONOUS_SECRETIONS_CARPET.get().getDefaultState());
                }
            }
        }
        this.kill();

    }
}
