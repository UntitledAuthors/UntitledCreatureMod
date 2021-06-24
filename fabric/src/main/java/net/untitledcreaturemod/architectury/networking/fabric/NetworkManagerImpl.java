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
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.untitledcreaturemod.architectury.networking.NetworkManager;
import net.untitledcreaturemod.architectury.networking.NetworkManager.NetworkReceiver;
import net.untitledcreaturemod.architectury.utils.Env;

public class NetworkManagerImpl {
    public static void registerReceiver(NetworkManager.Side side, Identifier id, NetworkReceiver receiver) {
        if (side == NetworkManager.Side.C2S) {
            registerC2SReceiver(id, receiver);
        } else if (side == NetworkManager.Side.S2C) {
            registerS2CReceiver(id, receiver);
        }
    }
    
    private static void registerC2SReceiver(Identifier id, NetworkReceiver receiver) {
        ServerSidePacketRegistry.INSTANCE.register(id, (packetContext, buf) -> receiver.receive(buf, to(packetContext)));
    }
    
    @Environment(EnvType.CLIENT)
    private static void registerS2CReceiver(Identifier id, NetworkReceiver receiver) {
        ClientSidePacketRegistry.INSTANCE.register(id, (packetContext, buf) -> receiver.receive(buf, to(packetContext)));
    }
    
    private static NetworkManager.PacketContext to(PacketContext context) {
        return new NetworkManager.PacketContext() {
            @Override
            public PlayerEntity getPlayer() {
                return context.getPlayer();
            }
            
            @Override
            public void queue(Runnable runnable) {
                context.getTaskQueue().execute(runnable);
            }
            
            @Override
            public Env getEnvironment() {
                return Env.fromPlatform(context.getPacketEnvironment());
            }
        };
    }
    
    public static Packet<?> toPacket(NetworkManager.Side side, Identifier id, PacketByteBuf buf) {
        if (side == NetworkManager.Side.C2S) {
            return toC2SPacket(id, buf);
        } else if (side == NetworkManager.Side.S2C) {
            return toS2CPacket(id, buf);
        }
        
        throw new IllegalArgumentException("Invalid side: " + side);
    }
    
    public static boolean canServerReceive(Identifier id) {
        return ClientSidePacketRegistry.INSTANCE.canServerReceive(id);
    }
    
    public static boolean canPlayerReceive(ServerPlayerEntity player, Identifier id) {
        return ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, id);
    }
    
    public static Packet<?> createAddEntityPacket(Entity entity) {
        return SpawnEntityPacket.create(entity);
    }
    
    @Environment(EnvType.CLIENT)
    private static Packet<?> toC2SPacket(Identifier id, PacketByteBuf buf) {
        return ClientSidePacketRegistry.INSTANCE.toPacket(id, buf);
    }
    
    private static Packet<?> toS2CPacket(Identifier id, PacketByteBuf buf) {
        return ServerSidePacketRegistry.INSTANCE.toPacket(id, buf);
    }
}
