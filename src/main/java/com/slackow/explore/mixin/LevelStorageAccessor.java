package com.slackow.explore.mixin;

import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(LevelStorage.class)
public interface LevelStorageAccessor {
    @Accessor
    void setSavesDirectory(Path saves);
}
