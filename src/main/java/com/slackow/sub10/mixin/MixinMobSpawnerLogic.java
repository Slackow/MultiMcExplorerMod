package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mixin(MobSpawnerLogic.class)
public abstract class MixinMobSpawnerLogic {
    @Shadow private int maxSpawnDelay;
    @Shadow private int minSpawnDelay;
    @Shadow private int spawnDelay;

    @Shadow public abstract World getWorld();

    @Shadow @Final private List<MobSpawnerEntry> spawnPotentials;

    @Shadow public abstract void setSpawnEntry(MobSpawnerEntry spawnEntry);

    @Shadow public abstract void sendStatus(int status);

    @Shadow @Nullable protected abstract Identifier getEntityId();

    @Shadow protected abstract boolean isPlayerInRange();

    @Shadow private double field_9159;
    @Shadow private double field_9161;

    @Shadow public abstract BlockPos getPos();

    @Shadow private int spawnCount;
    @Shadow private MobSpawnerEntry spawnEntry;
    @Shadow private int spawnRange;
    @Shadow private int maxNearbyEntities;

    @Shadow protected abstract void spawnEntity(Entity entity);

    /**
     * @author Slackow
     */
    @Overwrite
    public void update() {
        if (!this.isPlayerInRange()) {
            this.field_9159 = this.field_9161;
        } else {
            World world = this.getWorld();
            BlockPos blockPos = this.getPos();
            if (world.isClient) {
                double d = (double) blockPos.getX() + world.random.nextDouble();
                double e = (double) blockPos.getY() + world.random.nextDouble();
                double f = (double) blockPos.getZ() + world.random.nextDouble();
                world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0D, 0.0D, 0.0D);
                if (this.spawnDelay > 0) {
                    this.spawnDelay--;
                }

                this.field_9159 = this.field_9161;
                this.field_9161 = (this.field_9161 + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
            } else {
                if (this.spawnDelay == -1) {
                    this.updateSpawns();
                }

                if (this.spawnDelay > 0) {
                    this.spawnDelay--;
                    return;
                }

                boolean bl = false;
                int i = 0;

                while (true) {
                    if (i >= this.spawnCount) {
                        if (bl) {
                            this.updateSpawns();
                        }
                        break;
                    }

                    CompoundTag compoundTag = this.spawnEntry.getEntityTag();
                    Optional<EntityType<?>> optional = EntityType.fromTag(compoundTag);
                    if (!optional.isPresent()) {
                        this.updateSpawns();
                        return;
                    }

                    Random useThis = optional.get() == EntityType.BLAZE ? Randoms.BLAZE_POS_RANDOM : world.random;

                    ListTag listTag = compoundTag.getList("Pos", 6);
                    int j = listTag.size();
                    double g = j >= 1 ? listTag.getDouble(0) : (double) blockPos.getX() + (useThis.nextDouble() - useThis.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double h = j >= 2 ? listTag.getDouble(1) : (double) (blockPos.getY() + useThis.nextInt(3) - 1);
                    double k = j >= 3 ? listTag.getDouble(2) : (double) blockPos.getZ() + (useThis.nextDouble() - useThis.nextDouble()) * (double) this.spawnRange + 0.5D;
                    if (world.doesNotCollide(optional.get().createSimpleBoundingBox(g, h, k)) && SpawnRestriction.canSpawn((EntityType<?>) optional.get(), world.getWorld(), SpawnReason.SPAWNER, new BlockPos(g, h, k), useThis)) {
                        label97:
                        {
                            Entity entity = EntityType.loadEntityWithPassengers(compoundTag, world, (entityx) -> {
                                entityx.refreshPositionAndAngles(g, h, k, entityx.yaw, entityx.pitch);
                                return entityx;
                            });
                            if (entity == null) {
                                this.updateSpawns();
                                return;
                            }

                            int l = world.getNonSpectatingEntities(entity.getClass(), (new Box(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1)).expand(this.spawnRange)).size();
                            if (l >= this.maxNearbyEntities) {
                                this.updateSpawns();
                                return;
                            }

                            entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), useThis.nextFloat() * 360.0F, 0.0F);
                            if (entity instanceof MobEntity) {
                                MobEntity mobEntity = (MobEntity) entity;
                                if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
                                    break label97;
                                }

                                if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8)) {
                                    ((MobEntity) entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
                                }
                            }

                            this.spawnEntity(entity);
                            world.syncWorldEvent(2004, blockPos, 0);
                            if (entity instanceof MobEntity) {
                                ((MobEntity) entity).playSpawnEffects();
                            }

                            bl = true;
                        }
                    }

                    ++i;
                }
            }

        }
    }

    /**
     * @author Slackow
     */
    @Overwrite
    private void updateSpawns() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
            Random useThis = "blaze".equals(this.getEntityId().getPath()) ? Randoms.BLAZE_CYCLE_RANDOM : this.getWorld().random;
            this.spawnDelay = this.minSpawnDelay + useThis.nextInt(var10003);
        }

        if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }

        this.sendStatus(1);
    }
}
