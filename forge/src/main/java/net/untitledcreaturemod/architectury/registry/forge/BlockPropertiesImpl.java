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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.untitledcreaturemod.architectury.registry.BlockProperties;
import net.untitledcreaturemod.architectury.registry.ToolType;

import java.util.function.Function;

public class BlockPropertiesImpl {
    public static BlockProperties of(Material material, MaterialColor materialColor) {
        return new Impl(material, (state) -> materialColor);
    }
    
    public static BlockProperties of(Material material, Function<BlockState, MaterialColor> function) {
        return new Impl(material, function);
    }
    
    // public static BlockProperties copy(AbstractBlock abstractBlock) {
    //     return copy(abstractBlock.settings);
    // }

    // TODO: Migrate AW
    // public static BlockProperties copy(AbstractBlock.Settings old) {
    //     BlockProperties properties = of(old.material, old.materialColorFactory);
    //     properties.material = old.material;
    //     properties.destroyTime = old.hardness;
    //     properties.explosionResistance = old.resistance;
    //     properties.hasCollision = old.collidable;
    //     properties.isRandomlyTicking = old.randomTicks;
    //     properties.lightEmission = old.luminance;
    //     properties.materialColor = old.materialColorFactory;
    //     properties.soundType = old.soundGroup;
    //     properties.friction = old.slipperiness;
    //     properties.speedFactor = old.velocityMultiplier;
    //     properties.dynamicShape = old.dynamicBounds;
    //     properties.canOcclude = old.opaque;
    //     properties.isAir = old.isAir;
    //     properties.requiresCorrectToolForDrops = old.toolRequired;
    //     return properties;
    // }
    
    private static final class Impl extends BlockProperties {
        public Impl(Material material, Function<BlockState, MaterialColor> function) {
            super(material, function);
        }
        
        @Override
        public BlockProperties tool(ToolType type, int level) {
            harvestTool(net.minecraftforge.common.ToolType.get(type.forgeName));
            harvestLevel(level);
            return this;
        }
    }
}
