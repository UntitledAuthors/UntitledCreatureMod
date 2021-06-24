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

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemConvertible;
import java.util.Objects;
import java.util.function.Supplier;

public class ColorHandlersImpl {
    @SafeVarargs
    public static void registerItemColors(ItemColorProvider itemColor, Supplier<ItemConvertible>... items) {
        Objects.requireNonNull(itemColor, "color is null!");
        ColorProviderRegistry.ITEM.register(itemColor, unpackItems(items));
    }
    
    @SafeVarargs
    public static void registerBlockColors(BlockColorProvider blockColor, Supplier<Block>... blocks) {
        Objects.requireNonNull(blockColor, "color is null!");
        ColorProviderRegistry.BLOCK.register(blockColor, unpackBlocks(blocks));
    }
    
    private static ItemConvertible[] unpackItems(Supplier<ItemConvertible>[] items) {
        ItemConvertible[] array = new ItemConvertible[items.length];
        for (int i = 0; i < items.length; i++) {
            array[i] = Objects.requireNonNull(items[i].get());
        }
        return array;
    }
    
    private static Block[] unpackBlocks(Supplier<Block>[] blocks) {
        Block[] array = new Block[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            array[i] = Objects.requireNonNull(blocks[i].get());
        }
        return array;
    }
}
