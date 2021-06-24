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
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.platform.forge.EventBuses;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ColorHandlersImpl {
    private static final List<Pair<ItemColorProvider, Supplier<ItemConvertible>[]>> ITEM_COLORS = Lists.newArrayList();
    private static final List<Pair<BlockColorProvider, Supplier<Block>[]>> BLOCK_COLORS = Lists.newArrayList();

    static {
        EventBuses.onRegistered(UntitledCreatureMod.MOD_ID, bus -> {
            bus.register(ColorHandlersImpl.class);
        });
    }
    
    @SubscribeEvent
    public static void onItemColorEvent(ColorHandlerEvent.Item event) {
        for (Pair<ItemColorProvider, Supplier<ItemConvertible>[]> pair : ITEM_COLORS) {
            event.getItemColors().register(pair.getLeft(), unpackItems(pair.getRight()));
        }
    }
    
    @SubscribeEvent
    public static void onBlockColorEvent(ColorHandlerEvent.Block event) {
        for (Pair<BlockColorProvider, Supplier<Block>[]> pair : BLOCK_COLORS) {
            event.getBlockColors().registerColorProvider(pair.getLeft(), unpackBlocks(pair.getRight()));
        }
    }
    
    @SafeVarargs
    public static void registerItemColors(ItemColorProvider itemColor, Supplier<ItemConvertible>... items) {
        Objects.requireNonNull(itemColor, "color is null!");
        if (MinecraftClient.getInstance().getItemColors() == null) {
            ITEM_COLORS.add(Pair.of(itemColor, items));
        } else {
            MinecraftClient.getInstance().getItemColors().register(itemColor, unpackItems(items));
        }
    }
    
    @SafeVarargs
    public static void registerBlockColors(BlockColorProvider blockColor, Supplier<Block>... blocks) {
        Objects.requireNonNull(blockColor, "color is null!");
        if (MinecraftClient.getInstance().getBlockColors() == null) {
            BLOCK_COLORS.add(Pair.of(blockColor, blocks));
        } else {
            MinecraftClient.getInstance().getBlockColors().registerColorProvider(blockColor, unpackBlocks(blocks));
        }
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
