package com.lukeonuke.dispress.mixin;

import com.lukeonuke.dispress.Dispress;
import com.lukeonuke.dispress.discord.DiscordUtil;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.gui.screen.TitleScreen;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		Dispress.LOGGER.info("On main menu.");
		DiscordUtil.setPresence("At main menu", "Menu", "Not in session", true);
	}
}
