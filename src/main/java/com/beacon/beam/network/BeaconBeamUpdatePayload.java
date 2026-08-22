package com.beacon.beam.network;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.BeamInfinityViewDistance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BeaconBeamUpdatePayload(BeaconBeamData beacon, boolean active) implements CustomPacketPayload {

    public static final Identifier ID = BeamInfinityViewDistance.id("beacon_update");

    public static final CustomPacketPayload.Type<BeaconBeamUpdatePayload> TYPE =
            new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BeaconBeamUpdatePayload> CODEC = StreamCodec.composite(
            BeaconBeamData.STREAM_CODEC, BeaconBeamUpdatePayload::beacon,
            ByteBufCodecs.BOOL, BeaconBeamUpdatePayload::active,
            BeaconBeamUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}