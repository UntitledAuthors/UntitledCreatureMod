package com.untitledauthors.untitledcreaturemod;

import com.untitledauthors.untitledcreaturemod.items.ModSpawnEggItem;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


// TODO: This can probably be moved somewhere else, like CommonSetup
@Mod.EventBusSubscriber(modid = UntitledCreatureMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.initUnaddedEggs();
    }
}
