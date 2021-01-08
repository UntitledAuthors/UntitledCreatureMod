package com.untitledauthors.untitledcreaturemod.setup;

import com.untitledauthors.untitledcreaturemod.creature.toad.ToadEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;

public class CommonSetup {
    public static void setupCreatures() {
        GlobalEntityTypeAttributes.put(Registration.TOAD.get(), ToadEntity.getDefaultAttributes().create());
    }
}
