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

package net.untitledcreaturemod.architectury.registry.entity;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import java.util.function.Supplier;

public final class EntityAttributes {
    private EntityAttributes() {
    }
    
    /**
     * Registers default attributes to entities.
     *
     * @param type      the type of entity
     * @param attribute the attributes to register
     * @see net.minecraft.entity.attribute.DefaultAttributeRegistry
     */
    @ExpectPlatform
    public static void register(Supplier<EntityType<? extends LivingEntity>> type, Supplier<DefaultAttributeContainer.Builder> attribute) {
        throw new AssertionError();
    }
}
