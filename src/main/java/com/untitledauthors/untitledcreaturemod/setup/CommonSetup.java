package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.blopole.BlopoleEntity;
import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CommonSetup {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("untitledcreaturemod") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.TOAD_BUCKET.get());
        }
    };

    private static final List<ResourceLocation> TOAD_BIOMES = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:swamp"),
            new ResourceLocation("minecraft:swamp_hills")
    ));

    private static final List<ResourceLocation> ROCK_ANTELOPE_BIOMES = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:savanna_plateau"),
            new ResourceLocation("minecraft:savanna")
            // TODO: Add more savanna variants?
    ));

    private static final List<ResourceLocation> BLOPOLE_BIOMES = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:river"),
            new ResourceLocation("minecraft:jungle")
            // TODO: Add more jungle variants?
    ));

    // TODO: Move the setup into modules, similar to Quark or Charm, so user can configure spawning etc.
    public static void setupCreatures() {
        EntityType<ToadEntity> toad = Registration.TOAD.get();
        GlobalEntityTypeAttributes.put(toad, ToadEntity.getDefaultAttributes().create());
        EntitySpawnPlacementRegistry.register(toad, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ToadEntity::canAnimalSpawn);

        GlobalEntityTypeAttributes.put(Registration.ROCK_ANTELOPE.get(), RockAntelopeEntity.getDefaultAttributes().create());
        GlobalEntityTypeAttributes.put(Registration.BLOPOLE.get(), BlopoleEntity.getDefaultAttributes().create());

        MinecraftForge.EVENT_BUS.addListener(CommonSetup::onBiomeLoading);
    }

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (event.isCanceled()) {
            return;
        }
        setupSpawning(event, TOAD_BIOMES, Registration.TOAD.get(), 20, 3, 6);
        //setupSpawning(event, ROCK_ANTELOPE_BIOMES, Registration.ROCK_ANTELOPE.get(), 20, 3, 8);
        setupSpawning(event, BLOPOLE_BIOMES, Registration.BLOPOLE.get(), 20, 2, 5);
    }

    private static void setupSpawning(BiomeLoadingEvent event, List<ResourceLocation> biomeList,
                                      EntityType<? extends Entity> entityType, int weight, int minCount, int maxCount) {
        if (event.getName() == null)
            return;
        if (!biomeList.contains(event.getName()))
            return;
        List<MobSpawnInfo.Spawners> spawner = event.getSpawns().getSpawner(EntityClassification.CREATURE);
        spawner.add(new MobSpawnInfo.Spawners(entityType, weight, minCount, maxCount));
    }

}
