package com.beacon.beam.client;

import com.beacon.beam.network.BeaconBeamSyncPayload;
import com.beacon.beam.network.BeaconBeamUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class BeaconBeamClientNetworking {

    private static volatile boolean receivedServerSync = false;

    private BeaconBeamClientNetworking() {
    }

    public static boolean hasReceivedServerSync() {
        return receivedServerSync;
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(BeaconBeamSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    receivedServerSync = true;
                    BeaconBeamClientRegistry.replaceAll(payload.beacons());
                }));

        ClientPlayNetworking.registerGlobalReceiver(BeaconBeamUpdatePayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (payload.active()) {
                        BeaconBeamClientRegistry.activate(payload.beacon());
                    } else {
                        BeaconBeamClientRegistry.deactivate(payload.beacon());
                    }
                }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            BeaconBeamClientRegistry.clear();
            receivedServerSync = false;
        });
    }
}