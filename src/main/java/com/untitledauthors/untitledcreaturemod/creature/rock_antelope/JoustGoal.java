package com.untitledauthors.untitledcreaturemod.creature.rock_antelope;

import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class JoustGoal extends Goal {
    private final RockAntelopeEntity antelope;
    private final double moveSpeed;
    private final World world;
    // The position where the horn should drop in the animation
    private static final int ANIMATION_DROP_POSITION = 50;

    // State that gets reset
    private int animationTimer = 0;
    private Entity joustingPartner;

    public JoustGoal(RockAntelopeEntity entity, double moveSpeed) {
        this.antelope = entity;
        this.moveSpeed = moveSpeed;
        this.world = entity.getEntityWorld();
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        int joustingPartnerId = antelope.getJoustingPartner();
        if (joustingPartnerId == 0 || !antelope.canJoust() || !antelope.isAlive()) {
            return false;
        }
        this.joustingPartner = this.world.getEntityByID(joustingPartnerId);
        if (joustingPartner == null) {
            return false;
        }
        return joustingPartner.isAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return animationTimer < 50 && antelope.isAlive() && joustingPartner.isAlive() && antelope.getAttackingEntity() == null;
    }

    @Override
    public void tick() {
        // TODO: Better alignment when jousting
        // boolean facingPartner = antelope.getLookVec();
        // boolean facingPartner = false;

        // if (!facingPartner) {
        //     antelope.getLookController().setLookPositionWithEntity(joustingPartner, 300.0F, (float) antelope.getVerticalFaceSpeed());
        //     System.out.println(antelope.getYaw(1.0f));
        //     return;
        // } else {
        // }

        antelope.getLookController().setLookPositionWithEntity(joustingPartner, 300.0F, (float) antelope.getVerticalFaceSpeed());
        animationTimer++;
        if (animationTimer >= ANIMATION_DROP_POSITION) {
            dropHorn();
            this.antelope.setJoustingPartner(0);
        }
    }

    private void dropHorn() {
        // Leaders always win and keep their horns :)
        if (antelope.isLeader()) {
            return;
        }
        if (antelope.getRNG().nextInt(2) == 0) {
            world.playSound(null, antelope.getPosX(), antelope.getPosY(), antelope.getPosZ(),
                    SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 2.0f, 1.0f);

            // Break random horn
            if (antelope.getRNG().nextBoolean()) {
                antelope.setLeftHornPresent(false);
            } else {
                antelope.setRightHornPresent(false);
            }

            // TODO: Drop location is sometimes wrong
            System.out.println("Drop horn!");
            Vector3d lookVec = antelope.getLookVec();
            Vector3d spawnPos = antelope.getPositionVec().add(lookVec.mul(1.5f, 1.5f, 1.5f)).add(0, 1.2f, 0);
            ItemEntity hornEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                    new ItemStack(Registration.ANTELOPE_HORN.get()));
            world.addEntity(hornEntity);
        }
    }

    @Override
    public void resetTask() {
        animationTimer = 0;
        joustingPartner = null;
    }
}