package com.lukeonuke.dispress;

import com.lukeonuke.dispress.discord.DiscordUtil;
import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Objects;
import java.util.TimerTask;

public class MultiplayerPresence extends TimerTask {
    private final ClientPlayNetworkHandler handler;
    private final MinecraftClient client;
    private final PacketSender sender;

    public MultiplayerPresence(ClientPlayNetworkHandler handler, MinecraftClient client, PacketSender sender) {
        this.handler = handler;
        this.client = client;
        this.sender = sender;
    }

    @Override
    public void run() {
        Dispress.LOGGER.info("Refreshing RP");
        if(handler.getConnection().getAddress().toString().contains("local")){
            //Single-player
            Objects.requireNonNull(client.world);

            DiscordUtil.setPresence("Singleplayer", "In game", "In world " + client.world.getDimension().toString(), true);
        }else{
            Objects.requireNonNull(client.getCurrentServerEntry());

            Dispress.LOGGER.info("Current server is [" + client.getCurrentServerEntry().address + "]");
            ServerListing serverListing = ServerListing.getInstance();
            String IP = client.getCurrentServerEntry().address;
            DiscordRichPresence.Builder richPresenceBuilder = new DiscordRichPresence.Builder("Multiplayer").setDetails("In game").setBigImage("minecraft", "Multiplayer server");


            InetSocketAddress isa = (InetSocketAddress) handler.getConnection().getAddress();

            ServerPing ping = new ServerPing(isa);
            StatusResponse response;
            try {
                response = ping.fetchData();

                richPresenceBuilder.setDetails(String.format("In game (%d / %d)", response.getPlayers().getOnline(), response.getPlayers().getMax()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(serverListing.hasIconFor(IP)){
                richPresenceBuilder.setBigImage(serverListing.getIcon(IP), serverListing.getIcon(IP).toUpperCase(Locale.ROOT));
            }else{
                richPresenceBuilder.setBigImage("minecraft", "Multiplayer server");
            }

            DiscordRPC.discordUpdatePresence(richPresenceBuilder.build());
        }
    }

    @Override
    public boolean cancel() {
        DiscordUtil.setPresence("At main menu", "In menus", "Not in session", true);

        return super.cancel();
    }
}
