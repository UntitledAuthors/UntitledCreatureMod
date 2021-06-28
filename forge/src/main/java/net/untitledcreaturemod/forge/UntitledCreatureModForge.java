package net.untitledcreaturemod.forge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.untitledcreaturemod.ModSpawnEggItem;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.platform.forge.EventBuses;
import net.untitledcreaturemod.architectury.registry.RenderTypes;
import net.untitledcreaturemod.architectury.registry.entity.EntityRenderers;
import net.untitledcreaturemod.creature.pelican.Pelican;
import net.untitledcreaturemod.creature.toad.Toad;
import net.untitledcreaturemod.forge.renderers.PelicanEntityRenderer;
import net.untitledcreaturemod.forge.renderers.ToadEntityRenderer;

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
        EntityRenderers.register(Pelican.PELICAN.get(), PelicanEntityRenderer::new);
        RenderTypes.register(RenderLayer.getCutout(), Toad.POISONOUS_SECRETIONS_CARPET.get());
    }

    public void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.addModdedEggs();
    }
}
