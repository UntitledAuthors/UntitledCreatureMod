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

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.GenerationSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.SpawnSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.untitledcreaturemod.architectury.hooks.biome.BiomeHooks;
import net.untitledcreaturemod.architectury.hooks.biome.BiomeProperties;
import net.untitledcreaturemod.architectury.hooks.biome.GenerationProperties;
import net.untitledcreaturemod.architectury.hooks.biome.SpawnProperties;
import net.untitledcreaturemod.architectury.registry.BiomeModifications.BiomeContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class BiomeModificationsImpl {
    private static final Identifier FABRIC_MODIFICATION = new Identifier("architectury", "fabric_modification");
    private static final List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> ADDITIONS = Lists.newArrayList();
    private static final List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> POST_PROCESSING = Lists.newArrayList();
    private static final List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> REMOVALS = Lists.newArrayList();
    private static final List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> REPLACEMENTS = Lists.newArrayList();
    
    public static void addProperties(Predicate<BiomeContext> predicate, BiConsumer<BiomeContext, BiomeProperties.Mutable> modifier) {
        ADDITIONS.add(Pair.of(predicate, modifier));
    }
    
    public static void postProcessProperties(Predicate<BiomeContext> predicate, BiConsumer<BiomeContext, BiomeProperties.Mutable> modifier) {
        POST_PROCESSING.add(Pair.of(predicate, modifier));
    }
    
    public static void removeProperties(Predicate<BiomeContext> predicate, BiConsumer<BiomeContext, BiomeProperties.Mutable> modifier) {
        REMOVALS.add(Pair.of(predicate, modifier));
    }
    
    public static void replaceProperties(Predicate<BiomeContext> predicate, BiConsumer<BiomeContext, BiomeProperties.Mutable> modifier) {
        REPLACEMENTS.add(Pair.of(predicate, modifier));
    }
    
    static {
        BiomeModification modification = net.fabricmc.fabric.api.biome.v1.BiomeModifications.create(FABRIC_MODIFICATION);
        registerModification(modification, ModificationPhase.ADDITIONS, ADDITIONS);
        registerModification(modification, ModificationPhase.POST_PROCESSING, POST_PROCESSING);
        registerModification(modification, ModificationPhase.REMOVALS, REMOVALS);
        registerModification(modification, ModificationPhase.REPLACEMENTS, REPLACEMENTS);
    }
    
    private static void registerModification(BiomeModification modification, ModificationPhase phase, List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> list) {
        modification.add(phase, Predicates.alwaysTrue(), (biomeSelectionContext, biomeModificationContext) -> {
            BiomeContext biomeContext = wrapSelectionContext(biomeSelectionContext);
            BiomeProperties.Mutable mutableBiome = wrapMutableBiome(biomeSelectionContext.getBiome(), biomeModificationContext);
            for (Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>> pair : list) {
                if (pair.getLeft().test(biomeContext)) {
                    pair.getRight().accept(biomeContext, mutableBiome);
                }
            }
        });
    }
    
    private static BiomeContext wrapSelectionContext(BiomeSelectionContext context) {
        return new BiomeContext() {
            BiomeProperties properties = BiomeHooks.getBiomeProperties(context.getBiome());
            
            @Override
            @NotNull
            public Identifier getKey() {
                return context.getBiomeKey().getValue();
            }
            
            @Override
            @NotNull
            public BiomeProperties getProperties() {
                return properties;
            }
        };
    }
    
    private static BiomeProperties.Mutable wrapMutableBiome(Biome biome, BiomeModificationContext context) {
        return new BiomeHooks.MutableBiomeWrapped(
                biome,
                new MutableGenerationProperties(biome, context.getGenerationSettings()),
                new MutableSpawnProperties(biome, context.getSpawnSettings())
        ) {};
    }
    
    private static class MutableGenerationProperties extends BiomeHooks.GenerationSettingsWrapped implements GenerationProperties.Mutable {
        protected final GenerationSettingsContext context;
        
        public MutableGenerationProperties(Biome biome, GenerationSettingsContext context) {
            super(biome);
            this.context = context;
        }
        
        @Override
        public Mutable setSurfaceBuilder(ConfiguredSurfaceBuilder<?> builder) {
            this.context.setBuiltInSurfaceBuilder(builder);
            return this;
        }
        
        @Override
        public Mutable addFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature) {
            this.context.addBuiltInFeature(decoration, feature);
            return this;
        }
        
        @Override
        public Mutable addCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature) {
            context.addBuiltInCarver(carving, feature);
            return this;
        }
        
        @Override
        public Mutable addStructure(ConfiguredStructureFeature<?, ?> feature) {
            context.addBuiltInStructure(feature);
            return this;
        }
        
        @Override
        public Mutable removeFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature) {
            context.removeBuiltInFeature(decoration, feature);
            return this;
        }
        
        @Override
        public Mutable removeCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature) {
            context.removeBuiltInCarver(carving, feature);
            return this;
        }
        
        @Override
        public Mutable removeStructure(ConfiguredStructureFeature<?, ?> feature) {
            context.removeBuiltInStructure(feature);
            return this;
        }
    }
    
    private static class MutableSpawnProperties extends BiomeHooks.SpawnSettingsWrapped implements SpawnProperties.Mutable {
        protected final SpawnSettingsContext context;
        
        public MutableSpawnProperties(Biome biome, SpawnSettingsContext context) {
            super(biome);
            this.context = context;
        }
        
        @Override
        public @NotNull Mutable setCreatureProbability(float probability) {
            context.setCreatureSpawnProbability(probability);
            return this;
        }
        
        @Override
        public Mutable addSpawn(SpawnGroup category, SpawnSettings.SpawnEntry data) {
            context.addSpawn(category, data);
            return this;
        }
        
        @Override
        public boolean removeSpawns(BiPredicate<SpawnGroup, SpawnSettings.SpawnEntry> predicate) {
            return context.removeSpawns(predicate);
        }
        
        @Override
        public Mutable setSpawnCost(EntityType<?> entityType, SpawnSettings.SpawnDensity cost) {
            context.setSpawnCost(entityType, cost.getMass(), cost.getGravityLimit());
            return this;
        }
        
        @Override
        public Mutable setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            context.setSpawnCost(entityType, mass, gravityLimit);
            return this;
        }
        
        @Override
        public Mutable clearSpawnCost(EntityType<?> entityType) {
            context.clearSpawnCost(entityType);
            return this;
        }
        
        @Override
        public @NotNull Mutable setPlayerSpawnFriendly(boolean friendly) {
            context.setPlayerSpawnFriendly(friendly);
            return this;
        }
    }
}
