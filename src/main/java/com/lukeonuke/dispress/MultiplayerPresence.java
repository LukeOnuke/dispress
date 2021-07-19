package com.lukeonuke.dispress;

import com.lukeonuke.dispress.discord.DiscordUtil;
import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.ChatMessages;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.TimerTask;

public class MultiplayerPresence extends TimerTask {
    private ClientPlayNetworkHandler handler;
    private MinecraftClient client;
    private PacketSender sender;

    public MultiplayerPresence(ClientPlayNetworkHandler handler, MinecraftClient client, PacketSender sender) {
        this.handler = handler;
        this.client = client;
        this.sender = sender;
    }

    @Override
    public void run() {
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
            StatusResponse response = null;
            try {
                response = ping.fetchData();

                richPresenceBuilder.setDetails(String.format("In game (%d / %d)", response.getPlayers().getOnline(), response.getPlayers().getMax()));
                richPresenceBuilder.setBigImage("minecraft", response.getDescription().getText().replaceAll("/\\u00A7[0-9a-fk-or]/ig", ""));

                System.out.println(response.getPlayers().getOnline());
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(serverListing.hasIconFor(IP)){
                richPresenceBuilder.setSmallImage(serverListing.getIcon(IP), serverListing.getIcon(IP));
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
