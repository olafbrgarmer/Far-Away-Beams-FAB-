package com.beacon.beam.client.mixin;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.client.BeaconBeamClientNetworking;
import com.beacon.beam.client.BeaconBeamLocalDiscoveryRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public abstract class ClientBeaconBlockEntityMixin {

    @Shadow
    private int levels;

    @Unique
    private boolean beamInfinityLocal$wasActive = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private static void beamInfinityLocal$onTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (!level.isClientSide() || BeaconBeamClientNetworking.hasReceivedServerSync()) {
            return;
        }

        ClientBeaconBlockEntityMixin self = (ClientBeaconBlockEntityMixin) (Object) blockEntity;

        boolean isActiveNow = self.levels > 0;

        if (isActiveNow == self.beamInfinityLocal$wasActive) {
            return;
        }

        self.beamInfinityLocal$wasActive = isActiveNow;

        Identifier dimension = level.dimension().identifier();
        BeaconBeamData data = new BeaconBeamData(dimension, pos.immutable());

        if (isActiveNow) {
            BeaconBeamLocalDiscoveryRegistry.add(data);
        } else {
            BeaconBeamLocalDiscoveryRegistry.remove(data);
        }
    }
}