package com.beacon.beam.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class BeaconBeamClientEvents {

    private BeaconBeamClientEvents() {
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerData server = Minecraft.getInstance().getCurrentServer();
            if (server != null) {
                BeaconBeamLocalDiscoveryRegistry.load(server.ip);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BeaconBeamLocalDiscoveryRegistry.clear());
    }
}