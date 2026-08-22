package com.beacon.beam.client.mixin;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.client.BeaconBeamClientNetworking;
import com.beacon.beam.client.BeaconBeamLocalDiscoveryRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class ClientBlockEntityMixin {

    @Shadow
    public abstract Level getLevel();

    @Inject(method = "preRemoveSideEffects", at = @At("HEAD"))
    private void beamInfinityLocal$onPreRemoveSideEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!((Object) this instanceof BeaconBlockEntity)) {        //dear intellij... I KNOW THIS IS ALWAYS TRUE, OK?
            return;
        }

        Level level = this.getLevel();
        if (level == null || !level.isClientSide() || BeaconBeamClientNetworking.hasReceivedServerSync()) {
            return;
        }

        Identifier dimension = level.dimension().identifier();
        BeaconBeamLocalDiscoveryRegistry.remove(new BeaconBeamData(dimension, pos.immutable()));
    }
}