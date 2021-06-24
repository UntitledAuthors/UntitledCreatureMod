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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.untitledcreaturemod.architectury.registry.MenuRegistry;
import net.untitledcreaturemod.architectury.registry.menu.ExtendedMenuProvider;

public class MenuRegistryImpl {
    public static void openExtendedMenu(ServerPlayerEntity player, ExtendedMenuProvider provider) {
        NetworkHooks.openGui(player, provider, provider::saveExtraData);
    }
    
    public static <T extends ScreenHandler> ScreenHandlerType<T> of(MenuRegistry.SimpleMenuTypeFactory<T> factory) {
        return new ScreenHandlerType<>(factory::create);
    }
    
    public static <T extends ScreenHandler> ScreenHandlerType<T> ofExtended(MenuRegistry.ExtendedMenuTypeFactory<T> factory) {
        return IForgeContainerType.create(factory::create);
    }
    
    @OnlyIn(Dist.CLIENT)
    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void registerScreenFactory(ScreenHandlerType<? extends H> type, MenuRegistry.ScreenFactory<H, S> factory) {
        HandledScreens.register(type, factory::create);
    }
}
