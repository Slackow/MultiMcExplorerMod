package com.slackow.sub10.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {

    @Shadow public abstract ServerWorld getServerWorld();

    @Inject(method = "changeDimension", at = @At("TAIL"))
    public void changeDimensionMixin(ServerWorld destination, CallbackInfoReturnable<Entity> cir){

        if (getServerWorld().getRegistryKey().equals(World.END)) {

        }
    }
}
