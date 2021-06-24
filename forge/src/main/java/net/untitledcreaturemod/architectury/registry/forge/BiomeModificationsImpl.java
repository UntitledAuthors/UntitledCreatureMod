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
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.mixin.forge.BiomeGenerationSettingsBuilderAccessor;
import net.untitledcreaturemod.architectury.mixin.forge.MobSpawnSettingsBuilderAccessor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import net.untitledcreaturemod.architectury.hooks.biome.BiomeHooks;
import net.untitledcreaturemod.architectury.hooks.biome.BiomeProperties;
import net.untitledcreaturemod.architectury.hooks.biome.GenerationProperties;
import net.untitledcreaturemod.architectury.hooks.biome.SpawnProperties;
import net.untitledcreaturemod.architectury.registry.BiomeModifications.BiomeContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = UntitledCreatureMod.MOD_ID)
public class BiomeModificationsImpl {
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
    
    private static BiomeContext wrapSelectionContext(BiomeLoadingEvent event) {
        return new BiomeContext() {
            BiomeProperties properties = new BiomeWrapped(event);
            
            @Override
            @NotNull
            public Identifier getKey() {
                return event.getName();
            }
            
            @Override
            @NotNull
            public BiomeProperties getProperties() {
                return properties;
            }
        };
    }
    
    public static class BiomeWrapped implements BiomeProperties {
        protected final BiomeLoadingEvent event;
        protected final GenerationProperties generationProperties;
        protected final SpawnProperties spawnProperties;
        
        public BiomeWrapped(BiomeLoadingEvent event) {
            this(event,
                    new GenerationSettingsBuilderWrapped(event.getGeneration()),
                    new SpawnSettingsBuilderWrapped(event.getSpawns())
            );
        }
        
        public BiomeWrapped(BiomeLoadingEvent event,GenerationProperties generationProperties, SpawnProperties spawnProperties) {
            this.event = event;
            this.generationProperties = generationProperties;
            this.spawnProperties = spawnProperties;
        }

        @NotNull
        @Override
        public GenerationProperties getGenerationProperties() {
            return generationProperties;
        }
        
        @NotNull
        @Override
        public SpawnProperties getSpawnProperties() {
            return spawnProperties;
        }
    }

    private static class GenerationSettingsBuilderWrapped implements GenerationProperties {
        protected final BiomeGenerationSettingsBuilder generation;

        public GenerationSettingsBuilderWrapped(BiomeGenerationSettingsBuilder generation) {
            this.generation = generation;
        }

        @Override
        public @NotNull Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder() {
            return generation.getSurfaceBuilder();
        }

        @Override
        public @NotNull List<Supplier<ConfiguredCarver<?>>> getCarvers(GenerationStep.Carver carving) {
            return generation.getCarvers(carving);
        }

        @Override
        public List<Supplier<ConfiguredFeature<?, ?>>> getFeatures(GenerationStep.Feature decoration) {
            return generation.getFeatures(decoration);
        }

        @Override
        public @NotNull List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
            return ((BiomeGenerationSettingsBuilderAccessor) generation).getFeatures();
        }

        @Override
        public @NotNull List<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureStarts() {
            return generation.getStructures();
        }

    }

    private static class SpawnSettingsBuilderWrapped implements SpawnProperties {
        protected final MobSpawnInfoBuilder builder;

        public SpawnSettingsBuilderWrapped(MobSpawnInfoBuilder builder) {
            this.builder = builder;
        }

        @Override
        public float getCreatureProbability() {
            return builder.getProbability();
        }

        @Override
        public @NotNull Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> getSpawners() {
            return ((MobSpawnSettingsBuilderAccessor) builder).getSpawners();
        }

        @Override
        public @NotNull Map<EntityType<?>, SpawnSettings.SpawnDensity> getMobSpawnCosts() {
            return ((MobSpawnSettingsBuilderAccessor) builder).getSpawnCosts();
        }

        @Override
        public boolean isPlayerSpawnFriendly() {
            return ((MobSpawnSettingsBuilderAccessor) builder).isPlayerSpawnFriendly();
        }
    }

    
    public static class MutableBiomeWrapped extends BiomeWrapped implements BiomeProperties.Mutable {
        public MutableBiomeWrapped(BiomeLoadingEvent event) {
            super(event,
                    new MutableGenerationSettingsBuilderWrapped(event.getGeneration()),
                    new MutableSpawnSettingsBuilderWrapped(event.getSpawns())
            );
        }
        
        @Override
        public @NotNull GenerationProperties.Mutable getGenerationProperties() {
            return (GenerationProperties.Mutable) super.getGenerationProperties();
        }
        
        @Override
        public @NotNull SpawnProperties.Mutable getSpawnProperties() {
            return (SpawnProperties.Mutable) super.getSpawnProperties();
        }
    }
    
    private static class MutableGenerationSettingsBuilderWrapped extends GenerationSettingsBuilderWrapped implements GenerationProperties.Mutable {
        public MutableGenerationSettingsBuilderWrapped(BiomeGenerationSettingsBuilder generation) {
            super(generation);
        }
        
        @Override
        public Mutable setSurfaceBuilder(ConfiguredSurfaceBuilder<?> builder) {
            generation.surfaceBuilder(builder);
            return this;
        }
        
        @Override
        public Mutable addFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature) {
            generation.feature(decoration, feature);
            return this;
        }
        
        @Override
        public Mutable addCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature) {
            generation.carver(carving, feature);
            return this;
        }
        
        @Override
        public Mutable addStructure(ConfiguredStructureFeature<?, ?> feature) {
            generation.structureFeature(feature);
            return this;
        }
        
        @Override
        public Mutable removeFeature(GenerationStep.Feature decoration, ConfiguredFeature<?, ?> feature) {
            generation.getFeatures(decoration).removeIf(supplier -> supplier.get() == feature);
            return this;
        }
        
        @Override
        public Mutable removeCarver(GenerationStep.Carver carving, ConfiguredCarver<?> feature) {
            generation.getCarvers(carving).removeIf(supplier -> supplier.get() == feature);
            return this;
        }
        
        @Override
        public Mutable removeStructure(ConfiguredStructureFeature<?, ?> feature) {
            generation.getStructures().removeIf(supplier -> supplier.get() == feature);
            return this;
        }
    }
    
    private static class MutableSpawnSettingsBuilderWrapped extends SpawnSettingsBuilderWrapped implements SpawnProperties.Mutable {
        public MutableSpawnSettingsBuilderWrapped(MobSpawnInfoBuilder builder) {
            super(builder);
        }
        
        @Override
        public @NotNull Mutable setCreatureProbability(float probability) {
            builder.creatureSpawnProbability(probability);
            return this;
        }
        
        @Override
        public Mutable addSpawn(SpawnGroup category, SpawnSettings.SpawnEntry data) {
            builder.spawn(category, data);
            return this;
        }
        
        @Override
        public boolean removeSpawns(BiPredicate<SpawnGroup, SpawnSettings.SpawnEntry> predicate) {
            boolean removed = false;
            for (SpawnGroup type : builder.getSpawnerTypes()) {
                if (builder.getSpawner(type).removeIf(data -> predicate.test(type, data))) {
                    removed = true;
                }
            }
            return removed;
        }
        
        @Override
        public Mutable setSpawnCost(EntityType<?> entityType, SpawnSettings.SpawnDensity cost) {
            builder.spawnCost(entityType, cost.getMass(), cost.getGravityLimit());
            return this;
        }
        
        @Override
        public Mutable setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            builder.spawnCost(entityType, mass, gravityLimit);
            return this;
        }
        
        @Override
        public Mutable clearSpawnCost(EntityType<?> entityType) {
            getMobSpawnCosts().remove(entityType);
            return this;
        }
        
        @Override
        public @NotNull Mutable setPlayerSpawnFriendly(boolean friendly) {
            ((MobSpawnSettingsBuilderAccessor) builder).setPlayerSpawnFriendly(friendly);
            return this;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void processAdditions(BiomeLoadingEvent event) {
        modifyBiome(event, ADDITIONS);
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void processRemovals(BiomeLoadingEvent event) {
        modifyBiome(event, REMOVALS);
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void processReplacements(BiomeLoadingEvent event) {
        modifyBiome(event, REPLACEMENTS);
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postProcessBiomes(BiomeLoadingEvent event) {
        modifyBiome(event, POST_PROCESSING);
    }
    
    private static void modifyBiome(BiomeLoadingEvent event, List<Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>>> list) {
        BiomeContext biomeContext = wrapSelectionContext(event);
        BiomeProperties.Mutable mutableBiome = new MutableBiomeWrapped(event);
        for (Pair<Predicate<BiomeContext>, BiConsumer<BiomeContext, BiomeProperties.Mutable>> pair : list) {
            if (pair.getLeft().test(biomeContext)) {
                pair.getRight().accept(biomeContext, mutableBiome);
            }
        }
    }
}
