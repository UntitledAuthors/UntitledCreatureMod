package com.untitledauthors.untitledcreaturemod.creature.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public interface DirectedFleeingCreature extends FleeingCreature {
    void setCommonFleeTarget(Vector3d target);
    Vector3d getCommonFleeTarget();

    void alertOthersToFlee(LivingEntity attacker, Vector3d commonFleeTarget);
}
