package com.beacon.beam;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record BeaconBeamData(Identifier dimension, BlockPos pos) {
    //record the beacons data for the persistence
    public static final StreamCodec<RegistryFriendlyByteBuf, BeaconBeamData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BeaconBeamData::dimension,
            BlockPos.STREAM_CODEC, BeaconBeamData::pos,
            BeaconBeamData::new
    );
}