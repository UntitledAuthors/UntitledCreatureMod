package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.rock_antelope.RockAntelopeEntity;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadBucket;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import com.untitledauthors.untitledcreaturemod.items.ToadMobEggItem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class Registration {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // Mobs/Entities
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

    // Items
    public static final RegistryObject<ToadMobEggItem> TOAD_EGG = ITEMS.register("toad_egg", ToadMobEggItem::new);

    public static final RegistryObject<Item> RAW_TOAD_LEGS = ITEMS.register("raw_toad_legs",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(2).saturation(0.3F).meat().build()))));
    public static final RegistryObject<Item> COOKED_TOAD_LEGS = ITEMS.register("cooked_toad_legs",
            () -> new Item(((new Item.Properties()).group(ItemGroup.FOOD)
                    .food((new Food.Builder()).hunger(6).saturation(0.6F).meat().build()))));
    public static final RegistryObject<Item> TOAD_BUCKET = ITEMS.register("toad_bucket",
            () -> new ToadBucket(TOAD, new Item.Properties().maxStackSize(1).group(CommonSetup.ITEM_GROUP)));

    // Sounds
    public static final RegistryObject<SoundEvent> TOAD_AMBIENT = SOUNDS.register("toad_ambient",
            () -> new SoundEvent(new ResourceLocation(MODID, "toad_ambient")));


    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
    }
}
