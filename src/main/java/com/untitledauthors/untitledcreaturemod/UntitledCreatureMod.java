package com.untitledauthors.untitledcreaturemod;

import com.untitledauthors.untitledcreaturemod.setup.ClientSetup;
import com.untitledauthors.untitledcreaturemod.setup.CommonSetup;
import com.untitledauthors.untitledcreaturemod.setup.Registration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

@Mod(UntitledCreatureMod.MODID)
public class UntitledCreatureMod {
    public final static String MODID = "untitledcreaturemod";

    public UntitledCreatureMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        Registration.init();

        GeckoLibMod.DISABLE_IN_DEV = true;
        GeckoLib.initialize();
    }

    private void setup(final FMLCommonSetupEvent event) {
        CommonSetup.setupCreatures();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.setupEntityRenderers();
    }
}
