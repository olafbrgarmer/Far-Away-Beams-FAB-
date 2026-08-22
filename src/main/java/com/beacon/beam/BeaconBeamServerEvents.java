package com.beacon.beam;

import com.beacon.beam.network.BeaconBeamSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class BeaconBeamServerEvents {

    private BeaconBeamServerEvents() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(BeaconBeamRegistry::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(BeaconBeamRegistry::save);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(handler.getPlayer(), BeaconBeamSyncPayload.TYPE)) {
                BeaconBeamRegistry.sendFullSync(handler.getPlayer());
            }
        });
    }
}