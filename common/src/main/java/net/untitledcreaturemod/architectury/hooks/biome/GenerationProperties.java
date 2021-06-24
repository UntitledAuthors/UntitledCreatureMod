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

package net.untitledcreaturemod.architectury.hooks.biome;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

public interface GenerationProperties {
    Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder();
    
    List<Supplier<ConfiguredCarver<?>>> getCarvers(GenerationStep.Carver carving);
    
    List<Supplier<ConfiguredFeature<?, ?>>> getFeatures(GenerationStep.Feature decoration);
    
    List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures();
    
    List<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureStarts();
    
    interface Mutable extends GenerationProperties {
        Mutable setSurfaceBuilder(ConfiguredSurfaceBuilder<?> builder);
        
        Mutable addFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature);
        
        Mutable addCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature);
        
        Mutable addStructure(ConfiguredStructureFeature<?, ?> feature);
        
        Mutable removeFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature);
        
        Mutable removeCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature);
        
        Mutable removeStructure(ConfiguredStructureFeature<?, ?> feature);
    }
}
