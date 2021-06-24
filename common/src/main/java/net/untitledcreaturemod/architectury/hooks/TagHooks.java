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

package net.untitledcreaturemod.architectury.hooks;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import java.util.function.Supplier;

public final class TagHooks {
    private TagHooks() {
    }
    
    @ExpectPlatform
    public static <T> Tag.Identified<T> getOptional(Identifier id, Supplier<TagGroup<T>> collection) {
        throw new AssertionError();
    }
    
    public static Tag.Identified<Item> getItemOptional(Identifier id) {
        return getOptional(id, ItemTags::getTagGroup);
    }
    
    public static Tag.Identified<Block> getBlockOptional(Identifier id) {
        return getOptional(id, BlockTags::getTagGroup);
    }
    
    public static Tag.Identified<EntityType<?>> getEntityTypeOptional(Identifier id) {
        return getOptional(id, EntityTypeTags::getTagGroup);
    }
}
