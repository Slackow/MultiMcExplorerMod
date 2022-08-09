package com.slackow.explore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class InstanceSelectionScreen extends Screen {

    private static final Identifier DEFAULT_ICON = new Identifier("textures/multimc/default_icon.png");
    private final MultiMCManager manager = new MultiMCManager();
    private InstanceListWidget instanceListWidget;
    private ButtonWidget doneButton;

    public InstanceSelectionScreen() {
        super(new LiteralText("Select Instance"));
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
                manager.setSaves(client.runDirectory.toPath()
                        .getParent()
                        .resolveSibling(selected.instance.getPath())
                        .resolve(".minecraft/saves"));
                  manager.setSelected(selected.instance);

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
        client.openScreen(new SelectWorldScreen(null));
    }
    @Environment(EnvType.CLIENT)
    public class InstanceListWidget extends AlwaysSelectedEntryListWidget<InstanceListWidget.Entry> {

        public InstanceListWidget(MinecraftClient client) {
            super(client, InstanceSelectionScreen.this.width, InstanceSelectionScreen.this.height, 32, InstanceSelectionScreen.this.height - 65 + 4, 36);
            String name = manager.getSelected() != null ? manager.getSelected().getPath() :  client.runDirectory.toPath().getParent().getFileName().toString();
            System.out.println(manager.getSelected());
            for (Instance instance : InstanceSelectionScreen.this.manager.getInstances()) {
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
                textRenderer.drawWithShadow(matrices, instance.getName(), x + 35, y + 2, 0xffffff, false);
                textRenderer.drawWithShadow(matrices, instance.getGroup() + " - " + instance.getWorldCount() + " World" + (instance.getWorldCount() != 1 ? "s" : ""), x + 35, y + 18, 0x808080, false);
                textRenderer.drawWithShadow(matrices, " - /" + instance.getPath() + "/", x + 35 + textRenderer.getWidth(instance.getName()), y + 2, 0x808080, false);
                client.getTextureManager().bindTexture(DEFAULT_ICON);
                drawTexture(matrices, x + 1, y, 1, 0, 30, 32, 32, 32);
                drawTexture(matrices, x, y + 1, 0, 1, 1, 30, 32, 32);
                drawTexture(matrices, x + 31, y + 1, 31, 1, 1, 30, 32, 32);
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
