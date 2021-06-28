package net.untitledcreaturemod.fabric;

import net.fabricmc.api.ModInitializer;
import net.untitledcreaturemod.*;


public class UntitledCreatureModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UntitledCreatureMod.init();
        ModSpawnEggItem.addModdedEggs();
    }
}
