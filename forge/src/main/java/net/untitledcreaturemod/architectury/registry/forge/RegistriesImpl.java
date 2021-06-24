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

package net.untitledcreaturemod.architectury.registry.forge;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import net.untitledcreaturemod.architectury.core.RegistryEntry;
import net.untitledcreaturemod.architectury.platform.forge.EventBuses;
import net.untitledcreaturemod.architectury.registry.Registries;
import net.untitledcreaturemod.architectury.registry.Registry;
import net.untitledcreaturemod.architectury.registry.RegistrySupplier;
import net.untitledcreaturemod.architectury.registry.registries.RegistryBuilder;
import net.untitledcreaturemod.architectury.registry.registries.RegistryOption;
import net.untitledcreaturemod.architectury.registry.registries.StandardRegistryOption;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class RegistriesImpl {
    public static Registries.RegistryProvider _get(String modId) {
        return new RegistryProviderImpl(modId);
    }
    
    public static <T> Identifier getId(T t, RegistryKey<net.minecraft.util.registry.Registry<T>> registryKey) {
        if (t instanceof IForgeRegistryEntry)
            return ((IForgeRegistryEntry<?>) t).getRegistryName();
        return null;
    }
    
    public static <T> Identifier getId(T t, net.minecraft.util.registry.Registry<T> registry) {
        if (t instanceof IForgeRegistryEntry)
            return ((IForgeRegistryEntry<?>) t).getRegistryName();
        return null;
    }
    
    public static class RegistryProviderImpl implements Registries.RegistryProvider {
        private final String modId;
        private final IEventBus eventBus;
        private final Table<Type, RegistryObject<?>, Supplier<? extends IForgeRegistryEntry<?>>> registry = HashBasedTable.create();
        
        public RegistryProviderImpl(String modId) {
            this.modId = modId;
            this.eventBus = EventBuses.getModEventBus(modId).orElseThrow(() -> new IllegalStateException("Can't get event bus for mod '" + modId + "' because it was not registered!"));
            this.eventBus.register(new EventListener());
        }
        
        @Override
        public <T> Registry<T> get(RegistryKey<net.minecraft.util.registry.Registry<T>> registryKey) {
            return get(RegistryManager.ACTIVE.getRegistry(registryKey.getValue()));
        }
        
        public <T> Registry<T> get(IForgeRegistry registry) {
            return new ForgeBackedRegistryImpl<>(this.registry, registry);
        }
        
        @Override
        public <T> Registry<T> get(net.minecraft.util.registry.Registry<T> registry) {
            return new VanillaBackedRegistryImpl<>(registry);
        }
        
        @Override
        public <T extends RegistryEntry<T>> RegistryBuilder<T> builder(Class<T> type, Identifier registryId) {
            return new RegistryBuilderWrapper<>(this, new net.minecraftforge.registries.RegistryBuilder<>()
                    .setName(registryId)
                    .setType((Class) type));
        }
        
        public class EventListener {
            @SubscribeEvent
            public void handleEvent(RegistryEvent.Register event) {
                IForgeRegistry registry = event.getRegistry();
                
                for (Map.Entry<Type, Map<RegistryObject<?>, Supplier<? extends IForgeRegistryEntry<?>>>> row : RegistryProviderImpl.this.registry.rowMap().entrySet()) {
                    if (row.getKey() == event.getGenericType()) {
                        for (Map.Entry<RegistryObject<?>, Supplier<? extends IForgeRegistryEntry<?>>> entry : row.getValue().entrySet()) {
                            registry.register(entry.getValue().get());
                            entry.getKey().updateReference(registry);
                        }
                    }
                }
            }
        }
    }
    
    public static class RegistryBuilderWrapper<T extends RegistryEntry<T>> implements RegistryBuilder<T> {
        @NotNull
        private final RegistryProviderImpl provider;
        @NotNull
        private final net.minecraftforge.registries.RegistryBuilder<?> builder;
        private boolean saveToDisk = false;
        private boolean syncToClients = false;
        
        public RegistryBuilderWrapper(@NotNull RegistryProviderImpl provider, @NotNull net.minecraftforge.registries.RegistryBuilder<?> builder) {
            this.provider = provider;
            this.builder = builder;
        }
        
        @Override
        public @NotNull Registry<T> build() {
            if (!syncToClients) builder.disableSync();
            if (!saveToDisk) builder.disableSaving();
            return provider.get(builder.create());
        }
        
        @Override
        public @NotNull RegistryBuilder<T> option(@NotNull RegistryOption option) {
            if (option == StandardRegistryOption.SAVE_TO_DISC) {
                this.saveToDisk = true;
            } else if (option == StandardRegistryOption.SYNC_TO_CLIENTS) {
                this.syncToClients = true;
            }
            return this;
        }
    }
    
    public static class VanillaBackedRegistryImpl<T> implements Registry<T> {
        private net.minecraft.util.registry.Registry<T> delegate;
        
        public VanillaBackedRegistryImpl(net.minecraft.util.registry.Registry<T> delegate) {
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
        @Nullable
        public Identifier getId(T obj) {
            return delegate.getId(obj);
        }
        
        @Override
        public int getRawId(T obj) {
            return delegate.getRawId(obj);
        }
        
        @Override
        public Optional<RegistryKey<T>> getKey(T t) {
            return delegate.getKey(t);
        }
        
        @Override
        @Nullable
        public T get(Identifier id) {
            return delegate.get(id);
        }
        
        @Override
        public T byRawId(int rawId) {
            return delegate.get(rawId);
        }
        
        @Override
        public boolean contains(Identifier resourceLocation) {
            return delegate.getIds().contains(resourceLocation);
        }
        
        @Override
        public boolean containsValue(T t) {
            return delegate.getKey(t).isPresent();
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
        
        @Override
        public Iterator<T> iterator() {
            return delegate.iterator();
        }
    }
    
    public static class ForgeBackedRegistryImpl<T extends IForgeRegistryEntry<T>> implements Registry<T> {
        private IForgeRegistry<T> delegate;
        private Table<Type, RegistryObject<?>, Supplier<? extends IForgeRegistryEntry<?>>> registry;
        
        public ForgeBackedRegistryImpl(Table<Type, RegistryObject<?>, Supplier<? extends IForgeRegistryEntry<?>>> registry, IForgeRegistry<T> delegate) {
            this.registry = registry;
            this.delegate = delegate;
        }
        
        @Override
        public @NotNull RegistrySupplier<T> delegateSupplied(Identifier id) {
            Lazy<T> value = new Lazy<>(() -> get(id));
            return new RegistrySupplier<T>() {
                @Override
                public @NotNull Identifier getRegistryId() {
                    return delegate.getRegistryName();
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
            RegistryObject registryObject = RegistryObject.of(id, delegate);
            registry.put(delegate.getRegistrySuperType(), registryObject, () -> supplier.get().setRegistryName(id));
            return new RegistrySupplier<E>() {
                @Override
                public @NotNull Identifier getRegistryId() {
                    return delegate.getRegistryName();
                }
                
                @Override
                public @NotNull Identifier getId() {
                    return registryObject.getId();
                }
                
                @Override
                public boolean isPresent() {
                    return registryObject.isPresent();
                }
                
                @Override
                public E get() {
                    return (E) registryObject.get();
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
        @Nullable
        public Identifier getId(T obj) {
            return delegate.getKey(obj);
        }
        
        @Override
        public int getRawId(T obj) {
            return ((ForgeRegistry<T>) delegate).getID(obj);
        }
        
        @Override
        public Optional<RegistryKey<T>> getKey(T t) {
            return Optional.ofNullable(getId(t)).map(id -> RegistryKey.of(key(), id));
        }
        
        @Override
        @Nullable
        public T get(Identifier id) {
            return delegate.getValue(id);
        }
        
        @Override
        public T byRawId(int rawId) {
            return ((ForgeRegistry<T>) delegate).getValue(rawId);
        }
        
        @Override
        public boolean contains(Identifier resourceLocation) {
            return delegate.containsKey(resourceLocation);
        }
        
        @Override
        public boolean containsValue(T t) {
            return delegate.containsValue(t);
        }
        
        @Override
        public Set<Identifier> getIds() {
            return delegate.getKeys();
        }
        
        @Override
        public Set<Map.Entry<RegistryKey<T>, T>> entrySet() {
            return delegate.getEntries();
        }
        
        @Override
        public RegistryKey<? extends net.minecraft.util.registry.Registry<T>> key() {
            return RegistryKey.ofRegistry(delegate.getRegistryName());
        }
        
        @Override
        public Iterator<T> iterator() {
            return delegate.iterator();
        }
    }
}
