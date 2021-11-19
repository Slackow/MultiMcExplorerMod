package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

    /**
     * @author Slackow
     */
    @Overwrite
    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack){
        LootContext.Builder builder = new LootContext.Builder(world).random(state.getBlock() instanceof GravelBlock ? Randoms.GRAVEL_RANDOM : world.random)
                .parameter(LootContextParameters.POSITION, pos)
                .parameter(LootContextParameters.TOOL, stack)
                .optionalParameter(LootContextParameters.THIS_ENTITY, entity)
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(builder);
    }

    /**
     * @author Slackow
     */
    @Overwrite
    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity){
        LootContext.Builder builder = new LootContext.Builder(world).random(state.getBlock() instanceof GravelBlock ? Randoms.GRAVEL_RANDOM : world.random)
                .parameter(LootContextParameters.POSITION, pos)
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(builder);
    }
}
