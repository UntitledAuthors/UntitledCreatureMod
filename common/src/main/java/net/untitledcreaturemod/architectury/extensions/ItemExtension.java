/*
 * This file is part of architectury.
 * Copyright (C) 2020, 2021 architectury
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.untitledcreaturemod.architectury.extensions;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Extensions to {@link net.minecraft.item.Item}, implement this on to your item.
 */
public interface ItemExtension {
    /**
     * Invoked every tick when this item is equipped.
     *
     * @param stack  the item stack
     * @param player the player wearing the armor
     */
    default void tickArmor(ItemStack stack, PlayerEntity player) {
    }
    
    /**
     * Returns the {@link EquipmentSlot} for {@link ItemStack}.
     *
     * @param stack the item stack
     * @return the {@link EquipmentSlot}, return {@code null} to default to vanilla's {@link net.minecraft.entity.mob.MobEntity#getPreferredEquipmentSlot(ItemStack)}
     */
    @Nullable
    default EquipmentSlot getCustomEquipmentSlot(ItemStack stack) {
        return null;
    }
}
