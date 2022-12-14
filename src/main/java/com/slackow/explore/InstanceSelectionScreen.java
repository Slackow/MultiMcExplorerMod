package com.slackow.explore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;

public class InstanceSelectionScreen extends Screen {

    public static final Identifier DEFAULT_ICON = new Identifier("textures/multimc/default_icon.png");
    public static final Identifier SELECTED_INDICATOR = new Identifier("textures/multimc/selected.png");
    private final MultiMCManager manager = new MultiMCManager();
    private InstanceListWidget instanceListWidget;
    private ButtonWidget doneButton;
    private final boolean isSinglePlayer;

    public InstanceSelectionScreen(boolean isSinglePlayer) {
        super(new LiteralText("Select Instance"));
        this.isSinglePlayer = isSinglePlayer;
    }

    @Override
    protected void init() {
        this.instanceListWidget = new InstanceListWidget(client);
        this.children.add(instanceListWidget);
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, ScreenTexts.CANCEL, buttonWidget -> onClose()));
        doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            InstanceListWidget.Entry selected = instanceListWidget.getSelected();
            if (selected != null) {
                assert client != null;
                MultiMCManager.setSaves(MultiMCManager.getDotMinecraft()
                        .getParent()
                        .resolveSibling(selected.instance.getPath())
                        .resolve(".minecraft/saves"));
                MultiMCManager.setSelected(selected.instance);

            }
            onClose();
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        instanceListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, this.getTitle(), width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        assert client != null;
        client.openScreen(isSinglePlayer ? new SelectWorldScreen(null) : new MultiplayerScreen(null));
    }

    @Environment(EnvType.CLIENT)
    public class InstanceListWidget extends AlwaysSelectedEntryListWidget<InstanceListWidget.Entry> {

        public InstanceListWidget(MinecraftClient client) {
            super(client, InstanceSelectionScreen.this.width, InstanceSelectionScreen.this.height, 32, InstanceSelectionScreen.this.height - 65 + 4, 36);
            String name = manager.getSelected() != null ? manager.getSelected().getPath() : MultiMCManager.getDotMinecraft().getParent().getFileName().toString();
            for (Instance instance : manager.getInstances(isSinglePlayer)) {
                Entry instanceEntry = new Entry(instance);
                this.addEntry(instanceEntry);
                if (name.equals(instanceEntry.instance.getPath())) {
                    setSelected(instanceEntry);
                }
            }

            if (getSelected() != null) {
                centerScrollOn(getSelected());
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            InstanceSelectionScreen.this.renderBackground(matrices);
        }

        @Environment(EnvType.CLIENT)
        public final class Entry extends EntryListWidget.Entry<Entry> {

            private final Instance instance;
            private long time;

            public Entry(Instance instance) {
                this.instance = instance;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                textRenderer.drawWithShadow(matrices, instance.getName(), x + 35, y + 2, 0xFFFFFF, false);
                textRenderer.drawWithShadow(matrices, instance.getGroup() + " - " + instance.getItemCount() + " " + (isSinglePlayer ? "World" : "Server") + (instance.getItemCount() != 1 ? "s" : ""), x + 35, y + 18, 0x808080, false);
                textRenderer.drawWithShadow(matrices, " - /" + instance.getPath() + "/", x + 35 + textRenderer.getWidth(instance.getName()), y + 2, 0x808080, false);
                client.getTextureManager().bindTexture(DEFAULT_ICON);
                drawTexture(matrices, x + 1, y, 1, 0, 30, 32, 32, 32);
                drawTexture(matrices, x, y + 1, 0, 1, 1, 30, 32, 32);
                drawTexture(matrices, x + 31, y + 1, 31, 1, 1, 30, 32, 32);

                float red = 0;
                float green = 0;
                float blue = 0;
                if (instance.isThisOne()) {
                    red = 1;
                    green = 1;
                }
                //noinspection ConstantConditions
                if (red != 0 || green != 0 || blue != 0) {
                    client.getTextureManager().bindTexture(SELECTED_INDICATOR);
                    GL11.glColor4f(red, green, blue, 1);
                    drawTexture(matrices, x - 16, y + 12, 0, 0, 8, 8, 1, 1);
                }
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    if (Util.getMeasuringTimeMs() < time + 250) {
                        doneButton.onPress();
                    } else {
                        time = Util.getMeasuringTimeMs();
                    }
                    return true;
                } else {
                    return false;
                }
            }

            private void onPressed() {
                InstanceSelectionScreen.InstanceListWidget.this.setSelected(this);
            }
        }
    }
}
