package net.untitledcreaturemod.creature.toad;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.SpawnSettings;
import net.untitledcreaturemod.ModSpawnEggItem;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.registry.RegistrySupplier;
import net.untitledcreaturemod.creature.common.CreatureBucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static net.untitledcreaturemod.UntitledCreatureMod.*;

public class Toad {
    // Toad
    public static final RegistrySupplier<EntityType<ToadEntity>> TOAD = ENTITIES.register("toad",
            () -> EntityType.Builder.create(ToadEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(.8f, .5f)
                    .build("toad"));

    public static final RegistrySupplier<Item>SPAWN_EGG = ITEMS.register("toad_spawn_egg",
            () -> new ModSpawnEggItem(TOAD::get, 0x363e0a,0xdfd66b, new Item.Settings().group(CREATIVE_TAB)));

    public static final RegistrySupplier<Item> RAW_TOAD_LEGS = ITEMS.register("raw_toad_legs",
            () -> new Item(((new Item.Settings()).group(UntitledCreatureMod.CREATIVE_TAB)
                    .food((new FoodComponent.Builder()).hunger(2).saturationModifier(0.3F).meat().build()))));
    public static final RegistrySupplier<Item> COOKED_TOAD_LEGS = ITEMS.register("cooked_toad_legs",
            () -> new Item(((new Item.Settings()).group(UntitledCreatureMod.CREATIVE_TAB)
                    .food((new FoodComponent.Builder()).hunger(6).saturationModifier(0.6F).meat().build()))));

    public static final RegistrySupplier<Item> TOAD_BUCKET = ITEMS.register("toad_bucket",
            () -> new CreatureBucket(TOAD, new Item.Settings().maxCount(1).group(CREATIVE_TAB), Items.BUCKET));
    public static final RegistrySupplier<Item> POISONOUS_SECRETIONS_ITEM = ITEMS.register("poisonous_secretions",
            () -> new PoisonousSecretionsItem(new Item.Settings().maxCount(16).group(CREATIVE_TAB)));

    public static final RegistrySupplier<EntityType<PoisonousSecretionsEntity>> POISONOUS_SECRETIONS_PROJECTILE =
            ENTITIES.register("poisonous_secretions_projectile",
                    () -> EntityType.Builder.<PoisonousSecretionsEntity>create(PoisonousSecretionsEntity::new, SpawnGroup.MISC)
                            .setDimensions(0.25F, 0.25F).build("poisonous_secretions_entity"));

    public static final RegistrySupplier<Block> POISONOUS_SECRETIONS_CARPET = BLOCKS.register("poisonous_secretions_carpet", () -> new PoisonousSecretionsCarpet(
            AbstractBlock.Settings.copy(Blocks.SNOW).sounds(BlockSoundGroup.SLIME).nonOpaque().noCollision()));

    public static final RegistrySupplier<SoundEvent> AMBIENT_SOUND = SOUNDS.register("toad_ambient",
            () -> new SoundEvent(new Identifier(MOD_ID, "toad_ambient")));


    public static Supplier<SpawnSettings.SpawnEntry> SPAWN_ENTRY = () -> new SpawnSettings.SpawnEntry(TOAD.get(), 25, 3, 6);
    public static final ArrayList<Identifier> SPAWN_BIOMES = new ArrayList<>(Arrays.asList(
            new Identifier("minecraft:swamp"),
            new Identifier("minecraft:swamp_hills")
    ));
}
