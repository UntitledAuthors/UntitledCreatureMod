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

package net.untitledcreaturemod.architectury.hooks.biome;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.BiomeEffects.GrassColorModifier;
import net.minecraft.world.biome.BiomeParticleConfig;

public interface EffectsProperties {
    int getFogColor();
    
    int getWaterColor();
    
    int getWaterFogColor();
    
    int getSkyColor();
    
    OptionalInt getFoliageColorOverride();
    
    OptionalInt getGrassColorOverride();
    
    GrassColorModifier getGrassColorModifier();
    
    Optional<BiomeParticleConfig> getAmbientParticle();
    
    Optional<SoundEvent> getAmbientLoopSound();
    
    Optional<BiomeMoodSound> getAmbientMoodSound();
    
    Optional<BiomeAdditionsSound> getAmbientAdditionsSound();
    
    Optional<MusicSound> getBackgroundMusic();
    
    interface Mutable extends EffectsProperties {
        Mutable setFogColor(int color);
        
        Mutable setWaterColor(int color);
        
        Mutable setWaterFogColor(int color);
        
        Mutable setSkyColor(int color);
        
        Mutable setFoliageColorOverride(@Nullable Integer colorOverride);
        
        Mutable setGrassColorOverride(@Nullable Integer colorOverride);
        
        Mutable setGrassColorModifier(GrassColorModifier modifier);
        
        Mutable setAmbientParticle(@Nullable BiomeParticleConfig settings);
        
        Mutable setAmbientLoopSound(@Nullable SoundEvent sound);
        
        Mutable setAmbientMoodSound(@Nullable BiomeMoodSound settings);
        
        Mutable setAmbientAdditionsSound(@Nullable BiomeAdditionsSound settings);
        
        Mutable setBackgroundMusic(@Nullable MusicSound music);
    }
}
