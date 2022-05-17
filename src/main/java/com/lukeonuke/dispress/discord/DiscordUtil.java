package com.lukeonuke.dispress.discord;

import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class DiscordUtil {
    public static void setPresence(@NotNull String state, @NotNull String details, @NotNull String bigImageText, boolean timestamp) {
        if (timestamp) {
            DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("minecraft", bigImageText).setStartTimestamps(Instant.now().toEpochMilli()).build());
        } else {
            DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("minecraft", bigImageText).build());
        }
    }

    public static void setPresence(@NotNull String state, @NotNull String details, @NotNull String bigImageText, @NotNull String smallImageKey, @NotNull String smallImageText, boolean timestamp) {
        if (timestamp) {
            DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("minecraft", bigImageText).setSmallImage(smallImageKey, smallImageText).setStartTimestamps(Instant.now().toEpochMilli()).build());
        } else {
            DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("minecraft", bigImageText).setSmallImage(smallImageKey, smallImageText).build());
        }
    }
}
