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
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemConvertible;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class ColorHandlers {
    private ColorHandlers() {
    }
    
    public static void registerItemColors(ItemColorProvider color, ItemConvertible... items) {
        Supplier<ItemConvertible>[] array = new Supplier[items.length];
        for (int i = 0; i < items.length; i++) {
            ItemConvertible item = Objects.requireNonNull(items[i], "items[i] is null!");
            array[i] = () -> item;
        }
        registerItemColors(color, array);
    }
    
    public static void registerBlockColors(BlockColorProvider color, Block... blocks) {
        Supplier<Block>[] array = new Supplier[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            Block block = Objects.requireNonNull(blocks[i], "blocks[i] is null!");
            array[i] = () -> block;
        }
        registerBlockColors(color, array);
    }
    
    @SafeVarargs
    @ExpectPlatform
    public static void registerItemColors(ItemColorProvider color, Supplier<ItemConvertible>... items) {
        throw new AssertionError();
    }
    
    @SafeVarargs
    @ExpectPlatform
    public static void registerBlockColors(BlockColorProvider color, Supplier<Block>... blocks) {
        throw new AssertionError();
    }
}
