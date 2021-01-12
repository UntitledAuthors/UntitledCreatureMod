package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import com.untitledauthors.untitledcreaturemod.creature.toad.*;
import com.untitledauthors.untitledcreaturemod.items.ModSpawnEggItem;
import com.untitledauthors.untitledcreaturemod.items.tools.CustomTierList;
import com.untitledauthors.untitledcreaturemod.items.tools.StonehornDaggerItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
                    .size(.8f, .4f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("toad"));
    public static final RegistryObject<EntityType<RockAntelopeEntity>> ROCK_ANTELOPE = ENTITIES.register("rock_antelope",
            () -> EntityType.Builder.create(RockAntelopeEntity::new, EntityClassification.CREATURE)
                    .size(.9f, 1.8f)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("rock_antelope"));

    // Misc Entities
    public static final RegistryObject<EntityType<PoisonousSecretionsEntity>> POISONOUS_SECRETIONS_PROJECTILE =
            ENTITIES.register("poisonous_secretions_projectile",
            () -> EntityType.Builder.<PoisonousSecretionsEntity>create(PoisonousSecretionsEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F).build("poisonous_secretions_entity"));

    // Items
    public static final RegistryObject<Item>ANTELOPE_EGG = ITEMS.register("antelope_egg",() -> new ModSpawnEggItem(ROCK_ANTELOPE, 0x65391C,0xA1501B, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item>TOAD_EGG = ITEMS.register("toad_egg",() -> new ModSpawnEggItem(TOAD, 0x363e0a,0xdfd66b, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));

    public static final RegistryObject<Item> RAW_TOAD_LEGS = ITEMS.register("raw_toad_legs",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(2).saturation(0.3F).meat().build()))));
    public static final RegistryObject<Item> COOKED_TOAD_LEGS = ITEMS.register("cooked_toad_legs",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(6).saturation(0.6F).meat().build()))));

    public static final RegistryObject<Item> RAW_ANTELOPE_MEAT = ITEMS.register("raw_antelope_meat",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(4).saturation(0.6F).meat().build()))));

    public static final RegistryObject<Item> COOKED_ANTELOPE_MEAT = ITEMS.register("cooked_antelope_meat",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(8).saturation(0.9F).meat().build()))));

    public static final RegistryObject<Item> ANTELOPE_HORN = ITEMS.register("antelope_horn", () -> new Item(new Item.Properties().maxStackSize(64).group(CommonSetup.ITEM_GROUP)));

    public static final RegistryObject<Item> STONEHORN_DAGGER = ITEMS.register("stonehorn_dagger", () -> new StonehornDaggerItem(CustomTierList.STONEHORN, 1, -2.3f, new Item.Properties().maxStackSize(1).group(ItemGroup.COMBAT)));

    public static final RegistryObject<Item> TOAD_BUCKET = ITEMS.register("toad_bucket",
            () -> new ToadBucket(TOAD, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> POISONOUS_SECRETIONS_ITEM = ITEMS.register("poisonous_secretions",
            () -> new PoisonousSecretionsItem(new Item.Properties().maxStackSize(16).group(CommonSetup.ITEM_GROUP)));
    // Sounds
    public static final RegistryObject<SoundEvent> TOAD_AMBIENT = SOUNDS.register("toad_ambient",
            () -> new SoundEvent(new ResourceLocation(MODID, "toad_ambient")));

    // Blocks
    public static final RegistryObject<Block> POISONOUS_SECRETIONS_CARPET = BLOCKS.register("poisonous_secretions_carpet", () -> new PoisonousSecretionsCarpet(
            AbstractBlock.Properties.from(Blocks.SNOW).sound(SoundType.SLIME).notSolid().doesNotBlockMovement().harvestTool(ToolType.SHOVEL)));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        SOUNDS.register(bus);
    }
}
