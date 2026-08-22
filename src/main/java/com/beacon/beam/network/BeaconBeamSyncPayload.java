package com.beacon.beam.network;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.BeamInfinityViewDistance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record BeaconBeamSyncPayload(List<BeaconBeamData> beacons) implements CustomPacketPayload {

    public static final Identifier ID = BeamInfinityViewDistance.id("beacon_sync");

    public static final CustomPacketPayload.Type<BeaconBeamSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BeaconBeamSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(java.util.ArrayList::new, BeaconBeamData.STREAM_CODEC), BeaconBeamSyncPayload::beacons,
            BeaconBeamSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}