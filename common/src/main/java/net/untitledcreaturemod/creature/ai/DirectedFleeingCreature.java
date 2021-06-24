package net.untitledcreaturemod.creature.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public interface DirectedFleeingCreature extends FleeingCreature {
    void setCommonFleeTarget(Vec3d target);
    Vec3d getCommonFleeTarget();

    void alertOthersToFlee(LivingEntity attacker, Vec3d commonFleeTarget);
}
