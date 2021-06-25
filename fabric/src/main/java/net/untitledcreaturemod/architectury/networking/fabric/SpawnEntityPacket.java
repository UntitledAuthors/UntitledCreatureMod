/*
 * This file is part of architectury.
 * Copyright (C) 2020, 2021 architectury
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.untitledcreaturemod.architectury.networking.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.untitledcreaturemod.architectury.networking.NetworkManager;

import java.util.UUID;

/**
 * @see net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 */
public class SpawnEntityPacket {
    private static final Identifier PACKET_ID = new Identifier("untitledcreaturemod", "spawn_entity_packet");
    
    @Environment(EnvType.CLIENT)
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), PACKET_ID, SpawnEntityPacket::receive);
    }
    
    public static Packet<?> create(Entity entity) {
        if (entity.world.isClient()) {
            throw new IllegalStateException("SpawnPacketUtil.create called on the logical client!");
        }
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buffer.writeUuid(entity.getUuid());
        buffer.writeVarInt(entity.getId());
        Vec3d position = entity.getPos();
        buffer.writeDouble(position.x);
        buffer.writeDouble(position.y);
        buffer.writeDouble(position.z);
        buffer.writeFloat(entity.getPitch());
        buffer.writeFloat(entity.getYaw());
        buffer.writeFloat(entity.getHeadYaw());
        Vec3d deltaMovement = entity.getVelocity();
        buffer.writeDouble(deltaMovement.x);
        buffer.writeDouble(deltaMovement.y);
        buffer.writeDouble(deltaMovement.z);
        return NetworkManager.toPacket(NetworkManager.s2c(), PACKET_ID, buffer);
    }
    
    @Environment(EnvType.CLIENT)
    public static void receive(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityTypeId = buf.readVarInt();
        UUID uuid = buf.readUuid();
        int id = buf.readVarInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float xRot = buf.readFloat();
        float yRot = buf.readFloat();
        float yHeadRot = buf.readFloat();
        double deltaX = buf.readDouble();
        double deltaY = buf.readDouble();
        double deltaZ = buf.readDouble();
        context.queue(() -> {
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityTypeId);
            if (entityType == null) {
                throw new IllegalStateException("Entity type (" + entityTypeId + ") is unknown, spawning at (" + x + ", " + y + ", " + z + ")");
            }
            if (MinecraftClient.getInstance().world == null) {
                throw new IllegalStateException("Client world is null!");
            }
            Entity entity = entityType.create(MinecraftClient.getInstance().world);
            if (entity == null) {
                throw new IllegalStateException("Created entity is null!");
            }
            entity.setUuid(uuid);
            entity.setId(id);
            entity.setPos(x, y, z);
            entity.updatePositionAndAngles(x, y, z, xRot, yRot);
            entity.setHeadYaw(yHeadRot);
            entity.setYaw(yHeadRot);
            MinecraftClient.getInstance().world.addEntity(id, entity);
            entity.setVelocityClient(deltaX, deltaY, deltaZ);
        });
    }
}
