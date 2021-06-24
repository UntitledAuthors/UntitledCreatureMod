package net.untitledcreaturemod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import net.untitledcreaturemod.architectury.registry.CreativeTabs;
import net.untitledcreaturemod.architectury.registry.DeferredRegister;
import net.untitledcreaturemod.architectury.registry.Registries;
import net.untitledcreaturemod.architectury.registry.RegistrySupplier;

import java.util.function.Supplier;

public class UntitledCreatureMod {
    public static final String MOD_ID = "untitledcreaturemod";
    // We can use this if we don't want to use DeferredRegister
    public static final Lazy<Registries> REGISTRIES = new Lazy<>(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final ItemGroup EXAMPLE_TAB = CreativeTabs.create(new Identifier(MOD_ID, "untitledcreaturemod"), new Supplier<ItemStack>() {
        @Override
        public ItemStack get() {
            return new ItemStack(EXAMPLE_ITEM.get());
        }
    });

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
            new Item(new Item.Settings().group(UntitledCreatureMod.EXAMPLE_TAB)));

    public static void init() {
        ITEMS.register();
    }
}
