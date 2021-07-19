package com.lukeonuke.dispress.mixin;

import com.lukeonuke.dispress.discord.DiscordUtil;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(at = @At("head"), method = "init()V")
    private void init(CallbackInfo info){
        DiscordUtil.setPresence("Browsing servers", "Im menus", "Minecraft", false);
    }
}
