package com.untitledauthors.untitledcreaturemod.creature.pelican;

import com.untitledauthors.untitledcreaturemod.creature.pelican.ai.TakeOffGoal;
import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class PelicanEntity extends AnimalEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");
    private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk");
    private static final AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim");
    private static final AnimationBuilder IDLE_SWIM_ANIM = new AnimationBuilder().addAnimation("swim_idle");

    private static final AnimationBuilder GLIDE_ANIM = new AnimationBuilder().addAnimation("glide");
    private static final AnimationBuilder DESCENDING_ANIM = new AnimationBuilder().addAnimation("descending");
    private static final AnimationBuilder ASCENDING_ANIM = new AnimationBuilder().addAnimation("ascending");
    private static final AnimationBuilder DIVE_ANIM = new AnimationBuilder().addAnimation("dive");

    public static DataParameter<Boolean> IS_FLYING = EntityDataManager.createKey(PelicanEntity.class, DataSerializers.BOOLEAN);
    private Vector3d orbitOffset = Vector3d.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;

    public PelicanEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);

        this.moveController = new MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 12.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.1D)
                .createMutableAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(IS_FLYING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new OrbitPointGoal());

        this.goalSelector.addGoal(1, new TakeOffGoal(this));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0F));

        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.setAnimation(chooseAnimation());
        return PlayState.CONTINUE;
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return 0;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        //isFlying = world.isRemote && !isInWater() && !this.onGround;

        // Slow fall speed when flapping
        // Vector3d velocity = this.getMotion();
        // if (!this.onGround && velocity.y < 0.0D) {
        //     this.setMotion(velocity.mul(1.0D, 0.6D, 1.0D));
        // }

        if (!world.isRemote()) {
            // This runs on server side
            if(isFlying() && (isOnGround() || isInWater())) {
                setFlying(false);
                System.out.println("Landed!");
            }
        }

    }

    private AnimationBuilder chooseAnimation() {
        boolean isInWater = isInWater();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.01) || !(limbSwingAmount < 0.01) : !(limbSwingAmount > -0.05) || !(limbSwingAmount < 0.05F);
        if (isFlying()) {
            return GLIDE_ANIM;
        }
        if (isMoving) {
            return isInWater ? SWIM_ANIM : WALK_ANIM;
        } else {
            return isInWater ? IDLE_SWIM_ANIM : IDLE_ANIM;
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (isFlying()) {
            travelFlight(travelVector);
            return;
        }

        super.travel(travelVector);
    }

    public boolean isFlying() {
        return dataManager.get(IS_FLYING);
    }

    public void setFlying(boolean flying) {
        dataManager.set(IS_FLYING, flying);
    }

    public void travelFlight(Vector3d travelVector) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale((double)0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.5D));
        } else {
            BlockPos ground = new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale((double)f));
        }

        this.func_233629_a_(this, false);
    }

    abstract class MoveGoal extends Goal {
        public MoveGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean isNearTarget() {
            return PelicanEntity.this.orbitOffset.squareDistanceTo(PelicanEntity.this.getPosX(), PelicanEntity.this.getPosY(), PelicanEntity.this.getPosZ()) < 4.0D;
        }
    }


    class MoveHelperController extends MovementController {
        private float speedFactor = 0.10F;

        public MoveHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        public void tick() {
            if (!PelicanEntity.this.isFlying()) {
                super.tick();
                return;
            }
            if (PelicanEntity.this.collidedHorizontally) {
                PelicanEntity.this.rotationYaw += 180.0F;
                this.speedFactor = 0.1F;
            }

            float orbitOffsetDistX = (float)(PelicanEntity.this.orbitOffset.x - PelicanEntity.this.getPosX());
            float orbitOffsetDistY = (float)(PelicanEntity.this.orbitOffset.y - PelicanEntity.this.getPosY());
            float orbitOffsetDistZ = (float)(PelicanEntity.this.orbitOffset.z - PelicanEntity.this.getPosZ());

            double dist = (double) MathHelper.sqrt(orbitOffsetDistX * orbitOffsetDistX + orbitOffsetDistZ * orbitOffsetDistZ);
            double d1 = 1.0D - (double)MathHelper.abs(orbitOffsetDistY * 0.7F) / dist;
            orbitOffsetDistX = (float)((double)orbitOffsetDistX * d1);
            orbitOffsetDistZ = (float)((double)orbitOffsetDistZ * d1);
            dist = (double)MathHelper.sqrt(orbitOffsetDistX * orbitOffsetDistX + orbitOffsetDistZ * orbitOffsetDistZ);
            double d2 = (double)MathHelper.sqrt(orbitOffsetDistX * orbitOffsetDistX + orbitOffsetDistZ * orbitOffsetDistZ + orbitOffsetDistY * orbitOffsetDistY);

            // Rotational Control
            float f3 = PelicanEntity.this.rotationYaw;
            float f4 = (float)MathHelper.atan2((double)orbitOffsetDistZ, (double)orbitOffsetDistX);
            float f5 = MathHelper.wrapDegrees(PelicanEntity.this.rotationYaw + 90.0F);
            float f6 = MathHelper.wrapDegrees(f4 * (180F / (float)Math.PI));
            PelicanEntity.this.rotationYaw = MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F;
            PelicanEntity.this.renderYawOffset = PelicanEntity.this.rotationYaw;
            if (MathHelper.degreesDifferenceAbs(f3, PelicanEntity.this.rotationYaw) < 3.0F) {
                this.speedFactor = MathHelper.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
            } else {
                this.speedFactor = MathHelper.approach(this.speedFactor, 0.2F, 0.025F);
            }

            float f7 = (float)(-(MathHelper.atan2((double)(-orbitOffsetDistY), dist) * (double)(180F / (float)Math.PI)));
            PelicanEntity.this.rotationPitch = f7;
            float f8 = PelicanEntity.this.rotationYaw + 90.0F;
            double velX = (double)(this.speedFactor * MathHelper.cos(f8 * ((float)Math.PI / 180F))) * Math.abs((double)orbitOffsetDistX / d2);
            double velZ = (double)(this.speedFactor * MathHelper.sin(f8 * ((float)Math.PI / 180F))) * Math.abs((double)orbitOffsetDistZ / d2);
            double velY = (double)(this.speedFactor * MathHelper.sin(f7 * ((float)Math.PI / 180F))) * Math.abs((double)orbitOffsetDistY / d2);
            Vector3d vector3d = PelicanEntity.this.getMotion();
            PelicanEntity.this.setMotion(vector3d.add((new Vector3d(velX, velY, velZ)).subtract(vector3d).scale(0.2D)));
        }
    }


    class OrbitPointGoal extends PelicanEntity.MoveGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float orbitDirection;

        private OrbitPointGoal() {
        }

        public boolean shouldExecute() {
            return PelicanEntity.this.isFlying();
            //return PelicanEntity.this.getAttackTarget() == null || PelicanEntity.this.attackPhase == PelicanEntity.AttackPhase.CIRCLE;
        }

        public void startExecuting() {
            this.radius = 5.0F + PelicanEntity.this.rand.nextFloat() * 10.0F;
            this.yOffset = -4.0F + PelicanEntity.this.rand.nextFloat() * 9.0F;
            this.orbitDirection = PelicanEntity.this.rand.nextBoolean() ? 1.0F : -1.0F;
            this.adjustDirection();
        }

        public void tick() {
            if (PelicanEntity.this.rand.nextInt(50) == 0) {
                this.yOffset = -4.0F + PelicanEntity.this.rand.nextFloat() * 9.0F;
            }

            if (PelicanEntity.this.rand.nextInt(250) == 0) {
                ++this.radius;
                if (this.radius > 15.0F) {
                    this.radius = 5.0F;
                    this.orbitDirection = -this.orbitDirection;
                }
            }

            if (PelicanEntity.this.rand.nextInt(450) == 0) {
                this.angle = PelicanEntity.this.rand.nextFloat() * 2.0F * (float)Math.PI;
                this.adjustDirection();
            }

            if (this.isNearTarget()) {
                this.adjustDirection();
            }

            if (PelicanEntity.this.orbitOffset.y < PelicanEntity.this.getPosY() && !PelicanEntity.this.world.isAirBlock(PelicanEntity.this.getPosition().down(1))) {
                //this.yOffset = Math.max(1.0F, this.yOffset);
                //this.adjustDirection();
            }

            if (PelicanEntity.this.orbitOffset.y > PelicanEntity.this.getPosY() && !PelicanEntity.this.world.isAirBlock(PelicanEntity.this.getPosition().up(1))) {
                //this.yOffset = Math.min(-1.0F, this.yOffset);
                //this.adjustDirection();
            }

        }

        private void adjustDirection() {
            if (BlockPos.ZERO.equals(PelicanEntity.this.orbitPosition)) {
                PelicanEntity.this.orbitPosition = PelicanEntity.this.getPosition();
            }

            this.angle += this.orbitDirection * 15.0F * ((float)Math.PI / 180F);
            PelicanEntity.this.orbitOffset = Vector3d.copy(PelicanEntity.this.orbitPosition).add((double)(
                    this.radius * MathHelper.cos(this.angle)),
                    (double)(-4.0F + this.yOffset),
                    (double)(this.radius * MathHelper.sin(this.angle)));
        }
    }

    public AnimationFactory getFactory() {
        return factory;
    }

    public static <T extends MobEntity> boolean canSpawn(EntityType<T> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockState bs = world.getBlockState(pos.down());
        int light = world.getLightSubtracted(pos, 0);

        return bs.isSolid() && light > 8;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity entity) {
        return Registration.PELICAN.get().create(world);
    }
}
