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

package net.untitledcreaturemod.architectury.registry;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.untitledcreaturemod.architectury.registry.menu.ExtendedMenuProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A utility class to register {@link ScreenHandlerType}s and {@link Screen}s for containers
 */
public final class MenuRegistry {
    private MenuRegistry() {
    }
    
    /**
     * Opens the menu.
     *
     * @param player    The player affected
     * @param provider  The {@link NamedScreenHandlerFactory} that provides the menu
     * @param bufWriter That writer that sends extra data for {@link ScreenHandlerType} created with {@link MenuRegistry#ofExtended(ExtendedMenuTypeFactory)}
     */
    public static void openExtendedMenu(ServerPlayerEntity player, NamedScreenHandlerFactory provider, Consumer<PacketByteBuf> bufWriter) {
        openExtendedMenu(player, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(PacketByteBuf buf) {
                bufWriter.accept(buf);
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
    
    /**
     * Opens the menu.
     *
     * @param player   The player affected
     * @param provider The {@link ExtendedMenuProvider} that provides the menu
     */
    @ExpectPlatform
    public static void openExtendedMenu(ServerPlayerEntity player, ExtendedMenuProvider provider) {
        throw new AssertionError();
    }
    
    /**
     * Opens the menu.
     *
     * @param player   The player affected
     * @param provider The {@link NamedScreenHandlerFactory} that provides the menu
     */
    public static void openMenu(ServerPlayerEntity player, NamedScreenHandlerFactory provider) {
        player.openHandledScreen(provider);
    }
    
    /**
     * Creates a simple {@link ScreenHandlerType}.
     *
     * @param factory A functional interface to create the {@link ScreenHandlerType} from an id (Integer) and inventory
     * @param <T>     The type of {@link ScreenHandler} that handles the logic for the {@link ScreenHandlerType}
     * @return The {@link ScreenHandlerType} for your {@link ScreenHandler}
     */
    @ExpectPlatform
    public static <T extends ScreenHandler> ScreenHandlerType<T> of(SimpleMenuTypeFactory<T> factory) {
        throw new AssertionError();
    }
    
    /**
     * Creates a extended {@link ScreenHandlerType}.
     *
     * @param factory A functional interface to create the {@link ScreenHandlerType} from an id (Integer), {@link PlayerInventory}, and {@link PacketByteBuf}
     * @param <T>     The type of {@link ScreenHandler} that handles the logic for the {@link ScreenHandlerType}
     * @return The {@link ScreenHandlerType} for your {@link ScreenHandler}
     */
    @ExpectPlatform
    public static <T extends ScreenHandler> ScreenHandlerType<T> ofExtended(ExtendedMenuTypeFactory<T> factory) {
        throw new AssertionError();
    }
    
    /**
     * Registers a Screen Factory on the client to display.
     *
     * @param type    The {@link ScreenHandlerType} the screen visualizes
     * @param factory A functional interface that is used to create new {@link Screen}s
     * @param <H>     The type of {@link ScreenHandler} for the screen
     * @param <S>     The type for the {@link Screen}
     */
    @Environment(EnvType.CLIENT)
    @ExpectPlatform
    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void registerScreenFactory(ScreenHandlerType<? extends H> type, ScreenFactory<H, S> factory) {
        throw new AssertionError();
    }
    
    /**
     * Creates new screens.
     *
     * @param <H> The type of {@link ScreenHandler} for the screen
     * @param <S> The type for the {@link Screen}
     */
    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface ScreenFactory<H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> {
        /**
         * Creates a new {@link S} that extends {@link Screen}
         *
         * @param containerMenu The {@link ScreenHandler} that controls the game logic for the screen
         * @param inventory     The {@link PlayerInventory} for the screen
         * @param component     The {@link Text} for the screen
         * @return A new {@link S} that extends {@link Screen}
         */
        S create(H containerMenu, PlayerInventory inventory, Text component);
    }
    
    /**
     * Creates simple menus.
     *
     * @param <T> The {@link ScreenHandler} type
     */
    @FunctionalInterface
    public interface SimpleMenuTypeFactory<T extends ScreenHandler> {
        /**
         * Creates a new {@link T} that extends {@link ScreenHandler}
         *
         * @param id The id for the menu
         * @return A new {@link T} that extends {@link ScreenHandler}
         */
        T create(int id, PlayerInventory inventory);
    }
    
    /**
     * Creates extended menus.
     *
     * @param <T> The {@link ScreenHandler} type
     */
    @FunctionalInterface
    public interface ExtendedMenuTypeFactory<T extends ScreenHandler> {
        /**
         * Creates a new {@link T} that extends {@link ScreenHandler}.
         *
         * @param id        The id for the menu
         * @param inventory The {@link PlayerInventory} for the menu
         * @param buf       The {@link PacketByteBuf} for the menu to provide extra data
         * @return A new {@link T} that extends {@link ScreenHandler}
         */
        T create(int id, PlayerInventory inventory, PacketByteBuf buf);
    }
}
