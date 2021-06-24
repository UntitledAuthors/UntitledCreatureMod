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

package net.untitledcreaturemod.architectury.registry.fabric;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import java.util.function.BiConsumer;

public class GameRuleFactoryImpl {
    public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
        return GameRuleFactory.createBooleanRule(defaultValue);
    }
    
    public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changedCallback) {
        return GameRuleFactory.createBooleanRule(defaultValue, changedCallback);
    }
    
    public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue) {
        return GameRuleFactory.createIntRule(defaultValue);
    }
    
    public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntRule> changedCallback) {
        return GameRuleFactory.createIntRule(defaultValue, changedCallback);
    }
}
