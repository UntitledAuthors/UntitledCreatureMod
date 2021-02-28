package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.common.CreatureBucket;
import com.untitledauthors.untitledcreaturemod.creature.blopole.BlopoleEntity;
import com.untitledauthors.untitledcreaturemod.creature.hercules_frog.HerculesFrogEntity;
import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import com.untitledauthors.untitledcreaturemod.creature.toad.*;
import com.untitledauthors.untitledcreaturemod.items.AntelopeWarHorn;
import com.untitledauthors.untitledcreaturemod.items.DebugItem;
import com.untitledauthors.untitledcreaturemod.items.LifeVestItem;
import com.untitledauthors.untitledcreaturemod.items.ModSpawnEggItem;
import com.untitledauthors.untitledcreaturemod.items.tools.ModItemTiers;
import com.untitledauthors.untitledcreaturemod.items.tools.StonehornDaggerItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class Registration {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // Creature Entities
    public static final RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("toad",
            () -> EntityType.Builder.create(ToadEntity::new, EntityClassification.CREATURE)
                    .size(.8f, .5f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("toad"));
    public static final RegistryObject<EntityType<RockAntelopeEntity>> ROCK_ANTELOPE = ENTITIES.register("rock_antelope",
            () -> EntityType.Builder.create(RockAntelopeEntity::new, EntityClassification.CREATURE)
                    .size(1.2f, 1.6f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("rock_antelope"));
    public static final RegistryObject<EntityType<BlopoleEntity>> BLOPOLE = ENTITIES.register("blopole",
            () -> EntityType.Builder.create(BlopoleEntity::new, EntityClassification.CREATURE)
                    .size(.9f, .8f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("blopole"));
    public static final RegistryObject<EntityType<HerculesFrogEntity>> HERCULES_FROG = ENTITIES.register("hercules_frog",
            () -> EntityType.Builder.create(HerculesFrogEntity::new, EntityClassification.CREATURE)
                    .size(2.5f, 2.5f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("hercules_frog"));

    // Misc Entities
    public static final RegistryObject<EntityType<PoisonousSecretionsEntity>> POISONOUS_SECRETIONS_PROJECTILE =
            ENTITIES.register("poisonous_secretions_projectile",
            () -> EntityType.Builder.<PoisonousSecretionsEntity>create(PoisonousSecretionsEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F).build("poisonous_secretions_entity"));

    // Items
    public static final RegistryObject<Item>ANTELOPE_EGG = ITEMS.register("antelope_spawn_egg",
            () -> new ModSpawnEggItem(ROCK_ANTELOPE, 0x65391C,0xA1501B, new Item.Properties().group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item>TOAD_EGG = ITEMS.register("toad_spawn_egg",
            () -> new ModSpawnEggItem(TOAD, 0x363e0a,0xdfd66b, new Item.Properties().group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item>BLOPOLE_EGG = ITEMS.register("blopole_spawn_egg",
            () -> new ModSpawnEggItem(BLOPOLE, 0x595e55,0x2D332A, new Item.Properties().group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item>HERCULES_FROG_EGG = ITEMS.register("hercules_frog_spawn_egg",
            () -> new ModSpawnEggItem(HERCULES_FROG, 0xdfaf4f,0x432011, new Item.Properties().group(CommonSetup.ITEM_GROUP)));

    // Toad Items
    public static final RegistryObject<Item> RAW_TOAD_LEGS = ITEMS.register("raw_toad_legs",
            () -> new Item(((new Item.Properties()).group(CommonSetup.ITEM_GROUP)
                    .food((new Food.Builder()).hunger(2).saturation(0.3F).meat().build()))));
    public static final RegistryObject<Item> COOKED_TOAD_LEGS = ITEMS.register("cooked_toad_legs",
            () -> new Item(((new Item.Properties()).group(CommonSetup.ITEM_GROUP)
                    .food((new Food.Builder()).hunger(6).saturation(0.6F).meat().build()))));

    public static final RegistryObject<Item> ANTELOPE_WAR_HORN = ITEMS.register("antelope_war_horn", () -> new AntelopeWarHorn(new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));

    public static final RegistryObject<Item> TOAD_BUCKET = ITEMS.register("toad_bucket",
            () -> new CreatureBucket(TOAD, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP), Items.BUCKET));
    public static final RegistryObject<Item> POISONOUS_SECRETIONS_ITEM = ITEMS.register("poisonous_secretions",
            () -> new PoisonousSecretionsItem(new Item.Properties().maxStackSize(16).group(CommonSetup.ITEM_GROUP)));

    // Antelope Items
    public static final RegistryObject<Item> RAW_ANTELOPE_MEAT = ITEMS.register("raw_antelope_meat",
            () -> new Item(((new Item.Properties()).group(CommonSetup.ITEM_GROUP)
                    .food((new Food.Builder()).hunger(4).saturation(0.6F).meat().build()))));
    public static final RegistryObject<Item> COOKED_ANTELOPE_MEAT = ITEMS.register("cooked_antelope_meat",
            () -> new Item(((new Item.Properties()).group(CommonSetup.ITEM_GROUP)
                    .food((new Food.Builder()).hunger(8).saturation(0.9F).meat().build()))));
    public static final RegistryObject<Item> ANTELOPE_HORN = ITEMS.register("antelope_horn",
            () -> new Item(new Item.Properties().maxStackSize(64).group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> STONEHORN_DAGGER = ITEMS.register("stonehorn_dagger",
            () -> new StonehornDaggerItem(ModItemTiers.STONEHORN, 1, -2.1f, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> HORN_PATTERN_ITEM = ITEMS.register("antelope_banner_pattern",
            () -> new BannerPatternItem(registerPattern("rock_antelope"), new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));

    // Sounds
    // TODO: Maybe move these into their own class
    public static final RegistryObject<SoundEvent> TOAD_AMBIENT = SOUNDS.register("toad_ambient",
            () -> new SoundEvent(new ResourceLocation(MODID, "toad_ambient")));
    public static final RegistryObject<SoundEvent> BLOPOLE_AMBIENT = SOUNDS.register("blopole_ambient",
            () -> new SoundEvent(new ResourceLocation(MODID, "blopole_ambient")));
    public static final RegistryObject<SoundEvent> WAR_HORN_SOUND = SOUNDS.register("war_horn",
            () -> new SoundEvent(new ResourceLocation(MODID, "war_horn")));

    // Toad Blocks
    public static final RegistryObject<Block> POISONOUS_SECRETIONS_CARPET = BLOCKS.register("poisonous_secretions_carpet", () -> new PoisonousSecretionsCarpet(
            AbstractBlock.Properties.from(Blocks.SNOW).sound(SoundType.SLIME).notSolid().doesNotBlockMovement().harvestTool(ToolType.SHOVEL)));

    // Blopole Items
    public static final RegistryObject<Item> BLOPOLE_BUCKET = ITEMS.register("blopole_bucket",
            () -> new CreatureBucket(BLOPOLE, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP), Items.WATER_BUCKET));

    // Hercules Frog Items
    public static final RegistryObject<Item> LIFE_VEST = ITEMS.register("life_vest",
            () -> new LifeVestItem((new Item.Properties()).group(CommonSetup.ITEM_GROUP)));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        SOUNDS.register(bus);
    }

    private static BannerPattern registerPattern(String name) {
        return BannerPattern.create(name.toUpperCase(), name, name, true);
    }

    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item",
            () -> new DebugItem(new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));
}
