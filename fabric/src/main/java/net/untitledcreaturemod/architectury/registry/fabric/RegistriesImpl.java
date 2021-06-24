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

import com.google.common.base.Objects;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.untitledcreaturemod.architectury.core.RegistryEntry;
import net.untitledcreaturemod.architectury.registry.Registries;
import net.untitledcreaturemod.architectury.registry.Registry;
import net.untitledcreaturemod.architectury.registry.RegistrySupplier;
import net.untitledcreaturemod.architectury.registry.registries.RegistryBuilder;
import net.untitledcreaturemod.architectury.registry.registries.RegistryOption;
import net.untitledcreaturemod.architectury.registry.registries.StandardRegistryOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class RegistriesImpl {
    public static Registries.RegistryProvider _get(String modId) {
        return RegistryProviderImpl.INSTANCE;
    }
    
    public static <T> Identifier getId(T object, RegistryKey<net.minecraft.util.registry.Registry<T>> fallback) {
        if (fallback == null)
            return null;
        return RegistryProviderImpl.INSTANCE.get(fallback).getId(object);
    }
    
    public static <T> Identifier getId(T object, net.minecraft.util.registry.Registry<T> fallback) {
        if (fallback == null)
            return null;
        return RegistryProviderImpl.INSTANCE.get(fallback).getId(object);
    }
    
    public enum RegistryProviderImpl implements Registries.RegistryProvider {
        INSTANCE;
        
        @Override
        public <T> Registry<T> get(RegistryKey<net.minecraft.util.registry.Registry<T>> key) {
            return new RegistryImpl<>((net.minecraft.util.registry.Registry<T>) net.minecraft.util.registry.Registry.REGISTRIES.get(key.getValue()));
        }
        
        @Override
        public <T> Registry<T> get(net.minecraft.util.registry.Registry<T> registry) {
            return new RegistryImpl<>(registry);
        }
        
        @Override
        @NotNull
        public <T extends RegistryEntry<T>> RegistryBuilder<T> builder(Class<T> type, Identifier registryId) {
            return new RegistryBuilderWrapper<>(FabricRegistryBuilder.createSimple(type, registryId));
        }
    }
    
    public static class RegistryBuilderWrapper<T extends RegistryEntry<T>> implements RegistryBuilder<T> {
        @NotNull
        private FabricRegistryBuilder<T, SimpleRegistry<T>> builder;
        
        public RegistryBuilderWrapper(@NotNull FabricRegistryBuilder<T, SimpleRegistry<T>> builder) {
            this.builder = builder;
        }
        
        @Override
        public @NotNull Registry<T> build() {
            return RegistryProviderImpl.INSTANCE.get(builder.buildAndRegister());
        }
        
        @Override
        public @NotNull RegistryBuilder<T> option(@NotNull RegistryOption option) {
            if (option == StandardRegistryOption.SAVE_TO_DISC) {
                this.builder.attribute(RegistryAttribute.PERSISTED);
            } else if (option == StandardRegistryOption.SYNC_TO_CLIENTS) {
                this.builder.attribute(RegistryAttribute.SYNCED);
            }
            return this;
        }
    }
    
    public static class RegistryImpl<T> implements Registry<T> {
        private net.minecraft.util.registry.Registry<T> delegate;
        
        public RegistryImpl(net.minecraft.util.registry.Registry<T> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public @NotNull RegistrySupplier<T> delegateSupplied(Identifier id) {
            Lazy<T> value = new Lazy<>(() -> get(id));
            return new RegistrySupplier<T>() {
                @Override
                public @NotNull Identifier getRegistryId() {
                    return delegate.getKey().getValue();
                }
                
                @Override
                public @NotNull Identifier getId() {
                    return id;
                }
                
                @Override
                public boolean isPresent() {
                    return contains(id);
                }
                
                @Override
                public T get() {
                    return value.get();
                }
                
                @Override
                public int hashCode() {
                    return Objects.hashCode(getRegistryId(), getId());
                }
                
                @Override
                public boolean equals(Object obj) {
                    if (this == obj) return true;
                    if (!(obj instanceof RegistrySupplier)) return false;
                    RegistrySupplier<?> other = (RegistrySupplier<?>) obj;
                    return other.getRegistryId().equals(getRegistryId()) && other.getId().equals(getId());
                }
                
                @Override
                public String toString() {
                    return getRegistryId().toString() + "@" + id.toString();
                }
            };
        }
        
        @Override
        public @NotNull <E extends T> RegistrySupplier<E> registerSupplied(Identifier id, Supplier<E> supplier) {
            net.minecraft.util.registry.Registry.register(delegate, id, supplier.get());
            return (RegistrySupplier<E>) delegateSupplied(id);
        }
        
        @Override
        public @Nullable Identifier getId(T obj) {
            return delegate.getId(obj);
        }
        
        @Override
        public int getRawId(T obj) {
            return delegate.getRawId(obj);
        }
        
        @Override
        public Optional<RegistryKey<T>> getKey(T obj) {
            return delegate.getKey(obj);
        }
        
        @Override
        public @Nullable T get(Identifier id) {
            return delegate.get(id);
        }
        
        @Override
        public T byRawId(int rawId) {
            return delegate.get(rawId);
        }
        
        @Override
        public boolean contains(Identifier id) {
            return delegate.getIds().contains(id);
        }
        
        @Override
        public boolean containsValue(T obj) {
            return delegate.getKey(obj).isPresent();
        }
        
        @Override
        public Set<Identifier> getIds() {
            return delegate.getIds();
        }
        
        @Override
        public Set<Map.Entry<RegistryKey<T>, T>> entrySet() {
            return delegate.getEntries();
        }
        
        @Override
        public RegistryKey<? extends net.minecraft.util.registry.Registry<T>> key() {
            return delegate.getKey();
        }
        
        @NotNull
        @Override
        public Iterator<T> iterator() {
            return delegate.iterator();
        }
    }
}
