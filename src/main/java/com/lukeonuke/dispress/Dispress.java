package com.lukeonuke.dispress;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lukeonuke.dispress.version.DispressVersion;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Util;
import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Environment(EnvType.CLIENT)
public class Dispress implements ModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Dispress.class);
    private Timer timer;
    private final DispressVersion currentVersion = new DispressVersion("2.1.0", true);
    private DispressVersion latestVersion = null;

    @Override
    public void onInitialize() {
        //Living dangerously arent we captain.

        LOGGER.info("Registered on fabric loader.");

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            LOGGER.info("Registered on discord as : " + user.username + "#" + user.discriminator + " (" + user.userId + ").");
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
            if (!isLatest()) {

                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(MutableText.of(new LiteralTextContent("§b[dispress]§r There is a newer version §a" + latestVersion.getVersion() + "§r, while yours is §4" + currentVersion.getVersion() + "§r.")));
            }
            if (!currentVersion.isFullRelease()) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(MutableText.of(new LiteralTextContent("§b[dispress]§r §lYou are running a non production ready version.")));
            }

            LOGGER.info("Joined game " + handler.getWorld().toString() + " ip " + handler.getConnection().getAddress().toString());
            timer = new Timer();
            timer.schedule(new MultiplayerPresence(handler, client, sender), new Date(), 5000L);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            LOGGER.info("Left game");
            timer.cancel();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));

        new Thread(this::isLatest, "dispress version checker").start();
    }

    private boolean isLatest() {
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClient.setName("com.lukeonuke.dispress");

        if (latestVersion == null) {
            try {
                Gson gson = new Gson();
                String rawResp = new String(httpClient.GET("https://api.github.com/repos/LukeOnuke/dispress/releases/latest").getContent());
                LOGGER.info(rawResp);
                JsonObject response = gson.fromJson(rawResp, JsonObject.class);

                latestVersion = new DispressVersion(response.get("tag_name").getAsString(), !response.get("prerelease").getAsBoolean());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        if (latestVersion.isFullRelease()) {
            return currentVersion.isGreaterThan(latestVersion);
        }

        return true;
    }

}
