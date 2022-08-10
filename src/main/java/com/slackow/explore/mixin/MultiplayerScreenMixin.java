package com.slackow.explore.mixin;

import com.slackow.explore.InstanceSelectionButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci){
        this.addButton(new InstanceSelectionButton(this, width / 2 + textRenderer.getWidth(title) / 2 + 16, 8));
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = 20))
    public int changeTitleHeight(int a) {
        return 8 + 10 - textRenderer.fontHeight / 2;
    }
}
