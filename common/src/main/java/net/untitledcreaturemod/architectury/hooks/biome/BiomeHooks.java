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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class BiomeHooks {
    public static BiomeProperties getBiomeProperties(Biome biome) {
        return new BiomeWrapped(biome);
    }
    
    public static class BiomeWrapped implements BiomeProperties {
        protected final Biome biome;
        protected final GenerationProperties generationProperties;
        protected final SpawnProperties spawnProperties;

        public BiomeWrapped(Biome biome) {
            this(biome,
                    new GenerationSettingsWrapped(biome),
                    new SpawnSettingsWrapped(biome));
        }

        public BiomeWrapped(Biome biome,
                            GenerationProperties generationProperties,
                            SpawnProperties spawnProperties) {
            this.biome = biome;
            this.generationProperties = generationProperties;
            this.spawnProperties = spawnProperties;
        }

        @Override
        public GenerationProperties getGenerationProperties() {
            return generationProperties;
        }

        @Override
        public SpawnProperties getSpawnProperties() {
            return spawnProperties;
        }
    }

    public static class MutableBiomeWrapped extends BiomeWrapped implements BiomeProperties.Mutable {
        public MutableBiomeWrapped(Biome biome,
                                   GenerationProperties.Mutable generationProperties,
                                   SpawnProperties.Mutable spawnProperties) {
            super(biome,
                    generationProperties,
                    spawnProperties);
        }

        @Override
        public GenerationProperties.Mutable getGenerationProperties() {
            return (GenerationProperties.Mutable) super.getGenerationProperties();
        }

        @Override
        public SpawnProperties.Mutable getSpawnProperties() {
            return (SpawnProperties.Mutable) super.getSpawnProperties();
        }
    }

    public static class GenerationSettingsWrapped implements GenerationProperties {
        protected final GenerationSettings settings;
        
        public GenerationSettingsWrapped(Biome biome) {
            this(biome.getGenerationSettings());
        }
        
        public GenerationSettingsWrapped(GenerationSettings settings) {
            this.settings = settings;
        }
        
        @Override
        public Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder() {
            return Optional.ofNullable(settings.getSurfaceBuilder());
        }
        
        @Override
        public List<Supplier<ConfiguredCarver<?>>> getCarvers(GenerationStep.Carver carving) {
            return settings.getCarversForStep(carving);
        }
        
        @Override
        public List<Supplier<ConfiguredFeature<?, ?>>> getFeatures(GenerationStep.Feature decoration) {
            return settings.getFeatures().get(decoration.ordinal());
        }
        
        @Override
        public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
            return settings.getFeatures();
        }
        
        @Override
        public List<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureStarts() {
            return (List<Supplier<ConfiguredStructureFeature<?, ?>>>) settings.getStructureFeatures();
        }
    }
    
    public static class SpawnSettingsWrapped implements SpawnProperties {
        protected final SpawnSettings settings;
        
        public SpawnSettingsWrapped(Biome biome) {
            this(biome.getSpawnSettings());
        }
        
        public SpawnSettingsWrapped(SpawnSettings settings) {
            this.settings = settings;
        }
        
        @Override
        public float getCreatureProbability() {
            return this.settings.getCreatureSpawnProbability();
        }
        
        @Override
        public Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> getSpawners() {
            return null;
        }
        
        @Override
        public Map<EntityType<?>, SpawnSettings.SpawnDensity> getMobSpawnCosts() {
            return null;
        }
        
        @Override
        public boolean isPlayerSpawnFriendly() {
            return this.settings.isPlayerSpawnFriendly();
        }
    }
}
