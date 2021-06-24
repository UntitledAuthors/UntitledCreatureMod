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

package net.untitledcreaturemod.architectury.networking;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.untitledcreaturemod.architectury.utils.Env;

import java.util.Objects;

public final class NetworkManager {
    @ExpectPlatform
    public static void registerReceiver(Side side, Identifier id, NetworkReceiver receiver) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Packet<?> toPacket(Side side, Identifier id, PacketByteBuf buf) {
        throw new AssertionError();
    }
    
    public static void sendToPlayer(ServerPlayerEntity player, Identifier id, PacketByteBuf buf) {
        Objects.requireNonNull(player, "Unable to send packet to a 'null' player!").networkHandler.sendPacket(toPacket(serverToClient(), id, buf));
    }
    
    public static void sendToPlayers(Iterable<ServerPlayerEntity> players, Identifier id, PacketByteBuf buf) {
        Packet<?> packet = toPacket(serverToClient(), id, buf);
        for (ServerPlayerEntity player : players) {
            Objects.requireNonNull(player, "Unable to send packet to a 'null' player!").networkHandler.sendPacket(packet);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static void sendToServer(Identifier id, PacketByteBuf buf) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(toPacket(clientToServer(), id, buf));
        } else {
            throw new IllegalStateException("Unable to send packet to the server while not in game!");
        }
    }
    
    @Environment(EnvType.CLIENT)
    @ExpectPlatform
    public static boolean canServerReceive(Identifier id) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean canPlayerReceive(ServerPlayerEntity player, Identifier id) {
        throw new AssertionError();
    }
    
    /**
     * Easy to use utility method to create an entity spawn packet.
     * This packet is needed everytime any mod adds a non-living entity.
     * The entity should override {@link Entity#createSpawnPacket()} to point to this method!
     *
     * @param entity The entity which should be spawned.
     * @return The ready to use packet to spawn the entity on the client.
     * @see Entity#createSpawnPacket()
     */
    @ExpectPlatform
    public static Packet<?> createAddEntityPacket(Entity entity) {
        throw new AssertionError();
    }
    
    @FunctionalInterface
    public interface NetworkReceiver {
        void receive(PacketByteBuf buf, PacketContext context);
    }
    
    public interface PacketContext {
        PlayerEntity getPlayer();
        
        void queue(Runnable runnable);
        
        Env getEnvironment();
        
        default EnvType getEnv() {
            return getEnvironment().toPlatform();
        }
    }
    
    public static Side s2c() {
        return Side.S2C;
    }
    
    public static Side c2s() {
        return Side.C2S;
    }
    
    public static Side serverToClient() {
        return Side.S2C;
    }
    
    public static Side clientToServer() {
        return Side.C2S;
    }
    
    public enum Side {
        S2C,
        C2S
    }
}
