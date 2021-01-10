package com.untitledauthors.untitledcreaturemod;

import com.untitledauthors.untitledcreaturemod.items.UCMSpawnEggItem;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = UntitledCreatureMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber
{
    private static final Logger LOGGER = LogManager.getLogger(UntitledCreatureMod.MODID + " Mod Event Subscriber");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        UCMSpawnEggItem.initUnaddedEggs();
    }
}
