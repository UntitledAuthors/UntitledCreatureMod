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

package net.untitledcreaturemod.architectury.networking.forge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.untitledcreaturemod.architectury.networking.NetworkManager;

import java.util.Set;

import static net.untitledcreaturemod.architectury.networking.forge.NetworkManagerImpl.C2S;
import static net.untitledcreaturemod.architectury.networking.forge.NetworkManagerImpl.SYNC_IDS;


@OnlyIn(Dist.CLIENT)
public class ClientNetworkingManager {
    public static void initClient() {
        NetworkManagerImpl.CHANNEL.addListener(NetworkManagerImpl.createPacketHandler(NetworkEvent.ServerCustomPayloadEvent.class, NetworkManagerImpl.S2C));
        MinecraftForge.EVENT_BUS.register(ClientNetworkingManager.class);
        
        NetworkManagerImpl.registerS2CReceiver(SYNC_IDS, (buffer, context) -> {
            Set<Identifier> receivables = NetworkManagerImpl.serverReceivables;
            int size = buffer.readInt();
            receivables.clear();
            for (int i = 0; i < size; i++) {
                receivables.add(buffer.readIdentifier());
            }
            NetworkManager.sendToServer(SYNC_IDS, NetworkManagerImpl.sendSyncPacket(C2S));
        });
    }
    
    public static PlayerEntity getClientPlayer() {
        return MinecraftClient.getInstance().player;
    }
    
    @SubscribeEvent
    public static void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        NetworkManagerImpl.serverReceivables.clear();
    }
}
