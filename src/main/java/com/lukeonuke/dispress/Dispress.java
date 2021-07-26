package com.lukeonuke.dispress;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;

@Environment(EnvType.CLIENT)
public class Dispress implements ModInitializer {
	public final static Logger LOGGER = LoggerFactory.getLogger(Dispress.class);
	private Timer timer;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Registered on fabric loader.");

		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			LOGGER.info("Registered on discord as : " + user.username + "#" + user.discriminator + " (" + user.userId +").");
		}).build();

		DiscordRPC.discordInitialize("866283581738319933", handlers, true);

		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				DiscordRPC.discordRunCallbacks();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {
				}
			}
		}, "DiscordCallbacks").start();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("Joined game " + handler.getWorld().toString() + " ip " + handler.getConnection().getAddress().toString());
			timer = new Timer();
			timer.schedule(new MultiplayerPresence(handler, client, sender), new Date(), 5000L);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			LOGGER.info("Left game");
			timer.cancel();
		});



		Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
	}
}
