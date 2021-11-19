package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = {"createWorlds"}, at = @At("TAIL"))
    public void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci){
        Randoms.setAll(this.getSeed());
    }

    public long getSeed() {
        return this.worlds.get(World.OVERWORLD).getSeed();
    }
}
