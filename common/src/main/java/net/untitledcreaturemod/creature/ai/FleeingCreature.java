package net.untitledcreaturemod.creature.ai;

import net.minecraft.entity.LivingEntity;

public interface FleeingCreature {
    LivingEntity getAttackingEntity();
    boolean shouldFlee();
    default boolean shouldJumpWhileFleeing() {
        return false;
    }
}
