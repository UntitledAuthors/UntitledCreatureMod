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

import net.fabricmc.fabric.impl.object.builder.BlockSettingsInternals;
import net.fabricmc.fabric.impl.object.builder.FabricBlockInternals;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.untitledcreaturemod.architectury.registry.BlockProperties;
import net.untitledcreaturemod.architectury.registry.ToolType;

import java.util.function.Function;

public class BlockPropertiesImpl {
    public static BlockProperties of(Material material, MaterialColor color) {
        return new Impl(material, (state) -> color);
    }
    
    public static BlockProperties of(Material material, Function<BlockState, MaterialColor> color) {
        return new Impl(material, color);
    }

    // TODO: Migrate AW from mojmap to yarn
    // public static BlockProperties copy(AbstractBlock old) {
    //     return copy(old.settings);
    // }
    //
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
    //     BlockSettingsInternals otherInternals = (BlockSettingsInternals) old;
    //     FabricBlockInternals.ExtraData extraData = otherInternals.getExtraData();
    //     if (extraData != null) {
    //         ((BlockSettingsInternals) properties).setExtraData(extraData);
    //     }
    //     return properties;
    // }
    
    private static final class Impl extends BlockProperties {
        public Impl(Material material, Function<BlockState, MaterialColor> function) {
            super(material, function);
        }
        
        @Override
        public BlockProperties tool(ToolType type, int level) {
            FabricBlockInternals.computeExtraData(this).addMiningLevel(type.fabricTag.get(), level);
            return this;
        }
    }
}
