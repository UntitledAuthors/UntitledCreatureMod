package net.untitledcreaturemod.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.untitledcreaturemod.architectury.networking.fabric.SpawnEntityPacket;
import net.untitledcreaturemod.architectury.registry.RenderTypes;
import net.untitledcreaturemod.architectury.registry.entity.EntityRenderers;
import net.untitledcreaturemod.creature.pelican.Pelican;
import net.untitledcreaturemod.creature.pelican.PelicanEntityRenderer;
import net.untitledcreaturemod.creature.toad.Toad;
import net.untitledcreaturemod.creature.toad.ToadEntityRenderer;

public class UntitledCreatureModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(Toad.TOAD.get(), ToadEntityRenderer::new);
        EntityRenderers.register(Toad.POISONOUS_SECRETIONS_PROJECTILE.get(), (dispatcher) -> new FlyingItemEntityRenderer<>(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
        EntityRenderers.register(Pelican.PELICAN.get(), PelicanEntityRenderer::new);
        SpawnEntityPacket.register();

        RenderTypes.register(RenderLayer.getCutout(), Toad.POISONOUS_SECRETIONS_CARPET.get());
    }
}
