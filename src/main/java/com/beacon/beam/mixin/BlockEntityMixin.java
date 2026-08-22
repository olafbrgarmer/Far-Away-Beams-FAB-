package com.beacon.beam.mixin;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.BeaconBeamRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
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
public abstract class BlockEntityMixin {

    @Shadow
    public abstract Level getLevel();

    @Inject(method = "preRemoveSideEffects", at = @At("HEAD"))
    private void beamInfinity$onPreRemoveSideEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!((Object) this instanceof BeaconBlockEntity)) {
            return;
        }

        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }

        Identifier dimension = level.dimension().identifier();
        BeaconBeamRegistry.deactivate(server, new BeaconBeamData(dimension, pos.immutable()));
    }
}