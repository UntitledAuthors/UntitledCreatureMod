package net.untitledcreaturemod.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.architectury.platform.forge.EventBuses;

@Mod(UntitledCreatureMod.MOD_ID)
public class UntitledCreatureModForge {
    public UntitledCreatureModForge() {
        EventBuses.registerModEventBus(UntitledCreatureMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        UntitledCreatureMod.init();
    }
}
