package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity extends LivingEntity {

    @Shadow private long lootTableSeed;

    protected MixinMobEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * @author Slackow
     */
    @Overwrite
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        if (this.getType() == EntityType.BLAZE) {
            return super.getLootContextBuilder(causedByPlayer, source).random(Randoms.BLAZE_ROD_RANDOM);
        }
        return super.getLootContextBuilder(causedByPlayer, source).random(this.lootTableSeed, this.random);
    }
}
