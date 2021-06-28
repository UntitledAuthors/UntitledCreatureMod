package net.untitledcreaturemod.creature.pelican;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

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

    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_FLY = 1;

    // private Vector3d orbitOffset = Vector3d.ZERO;
    // private BlockPos orbitPosition = BlockPos.ZERO;

    public PelicanEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder getDefaultAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1D);
        // TODO: Swim Speed Mixin
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));

        goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        goalSelector.add(3, new WanderAroundGoal(this, 1.0D));
        goalSelector.add(4, new LookAroundGoal(this));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        boolean isInWater = isTouchingWater();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = isInWater ? !(limbSwingAmount > -0.01F) || !(limbSwingAmount < 0.01F) : !(limbSwingAmount > -0.01F) || !(limbSwingAmount < 0.01F);
        AnimationBuilder anim = isInWater ? IDLE_SWIM_ANIM : IDLE_ANIM;
        if (isFlying()) {
            anim = GLIDE_ANIM;
        } else if (isMoving) {
            anim = isInWater ? SWIM_ANIM : WALK_ANIM;
        }
        controller.setAnimation(anim);
        return PlayState.CONTINUE;
    }

    private boolean isFlying() {
        return false;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
