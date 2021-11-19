package com.slackow.sub10.mixin;

import com.slackow.sub10.api.ChunkGeneratorSeedAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator implements ChunkGeneratorSeedAccessor
{
    @Shadow
    @Final
    private long field_24748;

    @Override
    public long getSeed() {
        return this.field_24748;
    }
}