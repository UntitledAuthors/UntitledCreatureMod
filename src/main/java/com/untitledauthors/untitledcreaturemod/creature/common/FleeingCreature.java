package com.untitledauthors.untitledcreaturemod.creature.common;

import net.minecraft.entity.LivingEntity;

public interface FleeingCreature {
    LivingEntity getFleeTarget();
    boolean shouldFlee();
}