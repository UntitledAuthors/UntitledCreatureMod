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

package net.untitledcreaturemod.architectury.registry.forge;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.untitledcreaturemod.UntitledCreatureMod;

import java.util.List;

@Mod.EventBusSubscriber(modid = UntitledCreatureMod.MOD_ID)
public class ReloadListenersImpl {
    private static List<ResourceReloadListener> serverDataReloadListeners = Lists.newArrayList();
    
    public static void registerReloadListener(ResourceType type, ResourceReloadListener listener) {
        if (type == ResourceType.SERVER_DATA) {
            serverDataReloadListeners.add(listener);
        } else if (type == ResourceType.CLIENT_RESOURCES) {
            registerClientReloadListener(listener);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void registerClientReloadListener(ResourceReloadListener listener) {
        ((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(listener);
    }
    
    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        for (ResourceReloadListener listener : serverDataReloadListeners) {
            event.addListener(listener);
        }
    }
}
