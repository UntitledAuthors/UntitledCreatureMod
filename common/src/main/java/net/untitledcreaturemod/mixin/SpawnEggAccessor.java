package net.untitledcreaturemod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SpawnEggItem.class)
public interface SpawnEggAccessor {
  // NOTE: Accessor break hot code reloading in architectury, so when experimenting, you could comment this
  @Accessor("SPAWN_EGGS")
  static Map<EntityType<?>, SpawnEggItem> getSpawnEggs() {
    throw new AssertionError();
  }
}