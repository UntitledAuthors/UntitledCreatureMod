package net.untitledcreaturemod.creature.pelican;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.SpawnSettings;
import net.untitledcreaturemod.ModSpawnEggItem;
import net.untitledcreaturemod.architectury.registry.RegistrySupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static net.untitledcreaturemod.UntitledCreatureMod.*;

public class Pelican {
    public static final RegistrySupplier<EntityType<PelicanEntity>> PELICAN = ENTITIES.register("pelican",
            () -> EntityType.Builder.create(PelicanEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(.8f, .5f)
                    .build("pelican"));

    public static final RegistrySupplier<Item>SPAWN_EGG = ITEMS.register("pelican_spawn_egg",
            () -> new ModSpawnEggItem(PELICAN::get, 0xfff8f3,0xfad575, new Item.Settings().group(CREATIVE_TAB)));


    public static Supplier<SpawnSettings.SpawnEntry> SPAWN_ENTRY = () -> new SpawnSettings.SpawnEntry(PELICAN.get(), 25, 3, 6);
    public static final ArrayList<Identifier> SPAWN_BIOMES = new ArrayList<>(Arrays.asList(
            new Identifier("minecraft:beach"),
            new Identifier("minecraft:stone_shore")
    ));
}
