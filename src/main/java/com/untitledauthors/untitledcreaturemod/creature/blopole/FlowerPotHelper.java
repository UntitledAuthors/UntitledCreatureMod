package com.untitledauthors.untitledcreaturemod.creature.blopole;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class enables access to FlowerPotBlock.fullPots which stores all filled blocks mapped by their content flower
 * item id.
 */
public class FlowerPotHelper {
    private static Map<ResourceLocation, Supplier<? extends Block>> fullPots = null;

    private static void initFullPots() {
        try {
            Field fullPotsField = FlowerPotBlock.class.getDeclaredField("fullPots");
            fullPotsField.setAccessible(true);
            try {
                fullPots = (Map<ResourceLocation, Supplier<? extends Block>>) fullPotsField.get(Blocks.FLOWER_POT);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Map<ResourceLocation, Supplier<? extends Block>> getFullPots() {
        if (fullPots == null) {
            initFullPots();
        }
        return fullPots;
    }
}
