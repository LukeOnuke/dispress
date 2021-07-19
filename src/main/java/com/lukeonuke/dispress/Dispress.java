package com.lukeonuke.dispress;

import com.lukeonuke.dispress.discord.DiscordUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Timer;

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

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("Joined game " + handler.getWorld().toString() + " ip " + handler.getConnection().getAddress().toString());
			timer = new Timer();
			timer.schedule(new MultiplayerPresence(handler, client, sender), 5000L);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			LOGGER.info("Left game");
			timer.cancel();
		});

		Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
	}
}
