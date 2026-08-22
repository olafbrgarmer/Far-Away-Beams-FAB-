package com.beacon.beam.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {

    private ModNetworking() {           //more useless code
    }

    public static void registerCommon() {
        PayloadTypeRegistry.clientboundPlay().register(BeaconBeamSyncPayload.TYPE, BeaconBeamSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BeaconBeamUpdatePayload.TYPE, BeaconBeamUpdatePayload.CODEC);
    }
}