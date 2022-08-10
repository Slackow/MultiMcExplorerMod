package com.slackow.explore;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static net.minecraft.item.Items.BARRIER;
import static net.minecraft.item.Items.GRASS_BLOCK;

public class InstanceSelectionButton extends ButtonWidget {

    public InstanceSelectionButton(Screen screen, int x, int y) {
        super(x, y, 20, 20, LiteralText.EMPTY, e -> {
            if (MultiMCManager.hasMultiMC()) {
                MinecraftClient client = MinecraftClient.getInstance();

                boolean isSinglePlayer = screen instanceof SelectWorldScreen;
                if (Screen.hasShiftDown()) {
                    screen.onClose();
                    MultiMCManager.setSaves(MultiMCManager.getDotMinecraft().resolve("saves"));
                    MultiMCManager.setSelected(new Instance("current", MultiMCManager.getDotMinecraft().getParent().getFileName() + "", "", 0, true));
                    if (isSinglePlayer) {
                        client.openScreen(new SelectWorldScreen(null));
                    } else {
                        client.openScreen(new MultiplayerScreen(null));
                    }
                } else {
                    client.openScreen(new InstanceSelectionScreen(isSinglePlayer));
                }
            }
        }, (ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) -> {
            // display tooltip
            screen.renderTooltip(matrices, !MultiMCManager.hasMultiMC() ?
                    Collections.singletonList(StringRenderable.plain("No MultiMC Detected")) :
                    Screen.hasShiftDown() ? Arrays.asList(StringRenderable.plain("Switch to"),
                            StringRenderable.plain("Main Instance")) :
                    Arrays.asList(StringRenderable.plain("Browse " + ((screen instanceof SelectWorldScreen) ? "world" : "server") + "s from"),
                            StringRenderable.plain("other instances")), mouseX, mouseY);
        });
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        TextRenderer textRenderer = client.textRenderer;
        itemRenderer.renderInGui(new ItemStack(GRASS_BLOCK), x + 2, y + 2);

        if (!MultiMCManager.hasMultiMC()) {
            itemRenderer.zOffset += 50;
            itemRenderer.renderInGui(new ItemStack(BARRIER), x + 2, y + 2);
            itemRenderer.zOffset -= 50;
        }

        Path myPath = MultiMCManager.getDotMinecraft().resolve("saves");
        Path saves = client.getLevelStorage().getSavesDirectory().normalize();
        if (!saves.equals(myPath)) {
            drawStringWithShadow(matrices, textRenderer, "/" + saves.getName(saves.getNameCount() - 3) + "/", x + 23, y + 10 - textRenderer.fontHeight / 2, 0x808080);
        }
    }
}
