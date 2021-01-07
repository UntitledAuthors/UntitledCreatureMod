package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntityRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.untitledauthors.untitledcreaturemod.UntitledCreatureMod.MODID;

public class Registration {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("toad", () -> EntityType.Builder.create(ToadEntity::new, EntityClassification.CREATURE)
            .size(.8f, .4f)
            .setShouldReceiveVelocityUpdates(false)
            .build("toad"));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
    }

    public static void setupCreatures() {
        GlobalEntityTypeAttributes.put(TOAD.get(), ToadEntity.getDefaultAttributes().create());
    }

    public static void setupCreatureRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(TOAD.get(), ToadEntityRenderer::new);
    }
}
