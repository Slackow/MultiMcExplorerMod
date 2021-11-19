package com.slackow.sub10.mixin;

import com.slackow.sub10.Randoms;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(PiglinBrain.class)
public class MixinPiglinBrain {
    /**
     * @author Slackow
     */
    @Overwrite
    private static List<ItemStack> getBarteredItem(PiglinEntity piglin) {
        LootTable lootTable = piglin.world.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
        return lootTable.generateLoot((new LootContext.Builder((ServerWorld) piglin.world)).parameter(LootContextParameters.THIS_ENTITY, piglin).random(Randoms.BARTER_RANDOM).build(LootContextTypes.BARTER));
    }
}
