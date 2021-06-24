package net.untitledcreaturemod.forge;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.untitledcreaturemod.ModSpawnEggItem;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.platform.forge.EventBuses;
import net.untitledcreaturemod.architectury.registry.RenderTypes;
import net.untitledcreaturemod.architectury.registry.entity.EntityRenderers;
import net.untitledcreaturemod.creature.toad.Toad;
import net.untitledcreaturemod.forge.renderers.ToadEntityRenderer;

import java.util.Map;

@Mod(UntitledCreatureMod.MOD_ID)
public class UntitledCreatureModForge {
    public UntitledCreatureModForge() {
        EventBuses.registerModEventBus(UntitledCreatureMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EventPriority.LOWEST, this::onPostRegisterEntities);
        UntitledCreatureMod.init();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        // NOTE: Ugh, since Forge and Fabric Geckolib is not using the same renderer package we need to do this
        EntityRenderers.register(Toad.TOAD.get(), ToadEntityRenderer::new);
        EntityRenderers.register(Toad.POISONOUS_SECRETIONS_PROJECTILE.get(), (dispatcher) -> new FlyingItemEntityRenderer<>(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
        RenderTypes.register(RenderLayer.getCutout(), Toad.POISONOUS_SECRETIONS_CARPET.get());
    }

    public void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        addModdedEggs();
    }

    public static void addModdedEggs() {
        final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
        ItemDispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getEntityType(stack.getTag());
                entityType.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
                stack.decrement(1);
                return stack;
            }
        };
        for (final SpawnEggItem egg : ModSpawnEggItem.UNADDED_EGGS) {
            EGGS.put(egg.getEntityType(null), egg);
            DispenserBlock.registerBehavior(egg, dispenserBehavior);
        }
        ModSpawnEggItem.UNADDED_EGGS.clear();
    }
}
