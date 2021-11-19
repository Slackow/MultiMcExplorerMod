package com.slackow.sub10.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ LevelLoadingScreen.class })
public class MixinLevelLoadingScreen extends Screen
{
    @Shadow
    @Final
    private WorldGenerationProgressTracker progressProvider;
    @Shadow
    private long field_19101;

    protected MixinLevelLoadingScreen(final Text title) {
        super(title);
    }

    @Shadow
    public static void drawChunkMap(MatrixStack matrixStack, WorldGenerationProgressTracker worldGenerationProgressTracker, int i, int j, int k, int l){}

    /**
     * @author Slackow
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        String string = "TST: " + MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
        long l = Util.getMeasuringTimeMs();
        if (l - this.field_19101 > 2000L) {
            this.field_19101 = l;
            NarratorManager.INSTANCE.narrate((new TranslatableText("narrator.loading", string)).getString());
        }

        int i = this.width / 2;
        int j = this.height / 2;

        drawChunkMap(matrices, this.progressProvider, i, j + 30, 2, 0);
        TextRenderer var10002 = this.textRenderer;
        this.drawCenteredString(matrices, var10002, string, i, j - 9 / 2 - 30, 16777215);
    }
}