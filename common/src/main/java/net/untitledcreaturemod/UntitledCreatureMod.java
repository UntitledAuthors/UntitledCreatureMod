package net.untitledcreaturemod;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.untitledcreaturemod.architectury.registry.BiomeModifications;
import net.untitledcreaturemod.architectury.registry.CreativeTabs;
import net.untitledcreaturemod.architectury.registry.DeferredRegister;
import net.untitledcreaturemod.architectury.registry.entity.EntityAttributes;
import net.untitledcreaturemod.creature.toad.Toad;
import net.untitledcreaturemod.creature.toad.ToadEntity;

import java.util.function.Supplier;

public class UntitledCreatureMod {
    public static final String MOD_ID = "untitledcreaturemod";
    public static final ItemGroup CREATIVE_TAB = CreativeTabs.create(new Identifier("untitledcreatturemod"), new Supplier<ItemStack>() {
        @Override
        public ItemStack get() {
            return new ItemStack(Toad.SPAWN_EGG.get());
        }
    });

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registry.BLOCK_KEY);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MOD_ID, Registry.SOUND_EVENT_KEY);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(MOD_ID, Registry.ENTITY_TYPE_KEY);

    public static void init() {
        // All of these registries are mostly created in the respective creature classes e.g. creature.Toad
        ITEMS.register();
        ENTITIES.register();
        BLOCKS.register();
        SOUNDS.register();

        BiomeModifications.addProperties((bc) -> Toad.SPAWN_BIOMES.contains(bc.getKey()),
                (bc, mod) -> mod.getSpawnProperties().addSpawn(SpawnGroup.CREATURE, Toad.SPAWN_ENTRY.get()));
        EntityAttributes.register(Toad.TOAD::get, ToadEntity::getDefaultAttributes);
    }
}
