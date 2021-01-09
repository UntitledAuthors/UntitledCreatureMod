package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import com.untitledauthors.untitledcreaturemod.items.ToadMobEggItem;
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
import java.util.Collections;
import java.util.List;


public class CommonSetup {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("untitledcreaturemod") {
        @Override
        public ItemStack createIcon() {
            //FIX THIS USING REGISTRY INSTEAD OF MESSY FIX
            return new ItemStack(ToadMobEggItem::new);
        }
    };

    private static final List<ResourceLocation> biomes = new ArrayList<>(Collections.singletonList(
            new ResourceLocation("minecraft:swamp")
    ));


    // TODO: Move the setup into modules, similar to Quark or Charm, so user can configure spawning etc.
    public static void setupCreatures() {
        EntityType<ToadEntity> toad = Registration.TOAD.get();
        GlobalEntityTypeAttributes.put(toad, ToadEntity.getDefaultAttributes().create());
        EntitySpawnPlacementRegistry.register(toad, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ToadEntity::canAnimalSpawn);
        MinecraftForge.EVENT_BUS.addListener(CommonSetup::onBiomeLoading);
    }

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (!event.isCanceled())
            tryAddEntityToSpawn(event);
    }

    private static void tryAddEntityToSpawn(BiomeLoadingEvent event) {
        if (event.getName() == null)
            return;

        if (!biomes.contains(event.getName()))
            return;

        List<MobSpawnInfo.Spawners> spawner = event.getSpawns().getSpawner(EntityClassification.CREATURE);
        spawner.add(new MobSpawnInfo.Spawners(Registration.TOAD.get(), 20, 3, 6));
    }
}
