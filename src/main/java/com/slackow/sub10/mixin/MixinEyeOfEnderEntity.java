package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnderEntity.class)
public abstract class MixinEyeOfEnderEntity extends Entity {

    @Shadow private boolean dropsItem;

    public MixinEyeOfEnderEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = { "moveTowards" }, at = @At("TAIL"))
    public void moveTowards(BlockPos pos, CallbackInfo ci){
        dropsItem = Randoms.EYE_RANDOM.nextInt(5) > 0;
    }
}
