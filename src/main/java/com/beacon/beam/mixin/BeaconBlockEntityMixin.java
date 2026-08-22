package com.beacon.beam.mixin;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.BeaconBeamRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
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
public abstract class BeaconBlockEntityMixin {

    @Shadow
    private int levels;

    @Unique
    private boolean beamInfinity$wasActive = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private static void beamInfinity$onTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }

        BeaconBlockEntityMixin self = (BeaconBlockEntityMixin) (Object) blockEntity;

        boolean isActiveNow = self.levels > 0;

        if (isActiveNow == self.beamInfinity$wasActive) {
            return;
        }

        self.beamInfinity$wasActive = isActiveNow;

        Identifier dimension = level.dimension().identifier();
        BeaconBeamData data = new BeaconBeamData(dimension, pos.immutable());

        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }

        if (isActiveNow) {
            BeaconBeamRegistry.activate(server, data);
        } else {
            BeaconBeamRegistry.deactivate(server, data);
        }
    }
}