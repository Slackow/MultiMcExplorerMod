package com.slackow.explore.mixin;

import com.slackow.explore.InstanceSelectionScreen;
import com.slackow.explore.MultiMCManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Collections;

import static net.minecraft.item.Items.BARRIER;
import static net.minecraft.item.Items.GRASS_BLOCK;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {

    protected SelectWorldScreenMixin(Text title) {
        super(title);
    }

    @Shadow private WorldListWidget levelList;

    @Inject(method = "init", at = @At("HEAD"))
    public void initMultiMcExplore(CallbackInfo ci) {

        this.addButton(new ButtonWidget(width / 2 + 110, 22, 20, 20, LiteralText.EMPTY, e -> {
            if (MultiMCManager.hasMultiMC()) {
                assert client != null;
                client.openScreen(new InstanceSelectionScreen());
            }
        }, (ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) -> {
            // display tooltip
            renderTooltip(matrices, !MultiMCManager.hasMultiMC() ? Collections.singletonList(StringRenderable.plain("No MultiMC Detected")) :
                    Arrays.asList(StringRenderable.plain("Browse worlds from"),
                            StringRenderable.plain("other instances")), mouseX, mouseY);
        } ) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                super.renderButton(matrices, mouseX, mouseY, delta);
                itemRenderer.renderInGui(new ItemStack(GRASS_BLOCK), x + 2, y + 2);
                if (!MultiMCManager.hasMultiMC()) {
                    itemRenderer.zOffset += 50;
                    itemRenderer.renderInGui(new ItemStack(BARRIER), x + 2, y + 2);
                    itemRenderer.zOffset -= 50;
                }
                drawStringWithShadow(matrices, textRenderer, levelList.children().size() + "", x, y + 30, 0xFFFFFF);
            }
        });
    }

}
