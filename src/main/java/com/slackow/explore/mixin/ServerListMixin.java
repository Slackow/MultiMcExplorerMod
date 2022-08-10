package com.slackow.explore.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.ServerList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin(ServerList.class)
public class ServerListMixin {
    @Redirect(method = "loadFile", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;runDirectory:Ljava/io/File;", opcode = Opcodes.GETFIELD))
    public File serverList(MinecraftClient client){
        return client.getLevelStorage().getSavesDirectory().normalize().getParent().toFile();
    }
}
