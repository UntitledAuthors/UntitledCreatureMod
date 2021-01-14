package com.untitledauthors.untitledcreaturemod.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.List;

public class DebugHUD {
    public static void onDraw(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MatrixStack ms = event.getMatrixStack();
            ClientPlayerEntity player = mc.player;
            double radius = 10;
            AxisAlignedBB alertBox = AxisAlignedBB.fromVector(player.getPositionVec()).grow(radius, 10.0D, radius);

            List<MobEntity> list = mc.world.getLoadedEntitiesWithinAABB(ToadEntity.class, alertBox);
            if (list.size() >= 1) {
                ToadEntity toad = (ToadEntity) list.get(0);
                // TODO: Use this to display actual useful information?
                LivingEntity attackTarget = toad.getAttackTarget();
                String revengeTargetStr = attackTarget == null ? "null" : attackTarget.getCachedUniqueIdString();
                mc.fontRenderer.drawStringWithShadow(ms,
                        String.format("attackTarget: %s", attackTarget),0, 0, 0xFFFFFF);
            }
        }
    }
}
