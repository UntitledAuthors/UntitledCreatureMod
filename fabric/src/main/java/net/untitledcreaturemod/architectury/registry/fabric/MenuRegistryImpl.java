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

package net.untitledcreaturemod.architectury.registry.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.untitledcreaturemod.architectury.registry.MenuRegistry.*;
import net.untitledcreaturemod.architectury.registry.menu.ExtendedMenuProvider;
import org.jetbrains.annotations.Nullable;

public class MenuRegistryImpl {
    public static void openExtendedMenu(ServerPlayerEntity player, ExtendedMenuProvider provider) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                provider.saveExtraData(buf);
            }
            
            @Override
            public Text getDisplayName() {
                return provider.getDisplayName();
            }
            
            @Nullable
            @Override
            public ScreenHandler createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
                return provider.createMenu(i, inventory, player);
            }
        });
    }

    // TODO: Migrate AW
    // public static <T extends ScreenHandler> ScreenHandlerType<T> of(SimpleMenuTypeFactory<T> factory) {
    //     return new ScreenHandlerType<>(factory::create);
    // }
    
    public static <T extends ScreenHandler> ScreenHandlerType<T> ofExtended(ExtendedMenuTypeFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory::create);
    }
    
    @Environment(EnvType.CLIENT)
    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void registerScreenFactory(ScreenHandlerType<? extends H> type, ScreenFactory<H, S> factory) {
        ScreenRegistry.register(type, factory::create);
    }
}
