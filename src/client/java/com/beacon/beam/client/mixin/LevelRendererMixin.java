package com.beacon.beam.client.mixin;


import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.client.BeaconBeamClientRegistry;
import com.beacon.beam.client.BeaconBeamLocalDiscoveryRegistry;
import com.beacon.beam.client.BeaconBeamViewConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// --- forcing beam render beyond the players render distance ------------
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@Inject(method = "submitBlockEntities", at = @At("TAIL"))
	private void beamInfinity$submitExtraBeaconBeams(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeStorage submitNodeStorage, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		ClientLevel level = client.level;
		if (level == null) {
			return;
		}

		Identifier currentDimension = level.dimension().identifier();
		Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
		float animationTime = (float) (level.getGameTime() % 40L);

		// Here the code checks if the beam is rendered or not BEFORE force a render
		Set<BlockPos> alreadyRendered = new HashSet<>();
		for (var renderState : levelRenderState.blockEntityRenderStates) {
			alreadyRendered.add(renderState.blockPos);
		}

		Set<BeaconBeamData> knownBeacons = new HashSet<>(BeaconBeamClientRegistry.getAll());
		knownBeacons.addAll(BeaconBeamLocalDiscoveryRegistry.getAll());

		// Checking if the conditions to render the beam are true
		List<Candidate> candidates = new ArrayList<>();
		for (BeaconBeamData beacon : knownBeacons) {
			if (!beacon.dimension().equals(currentDimension)) {
				continue;
			}

			BlockPos pos = beacon.pos();
			if (alreadyRendered.contains(pos)) {
				continue;
			}

			double distance = cameraPos.subtract(pos.getCenter()).horizontalDistance();
			candidates.add(new Candidate(pos, distance));
		}

		// Checking more conditions
		List<Candidate> toRender = switch (BeaconBeamViewConfig.getMode()) {
			case RADIUS -> {		//check if the beam can be rendered by the distance defined by the player (via "/beam view")
				int radius = BeaconBeamViewConfig.getRadius();
				yield candidates.stream().filter(c -> c.distance() <= radius).toList();
			}
			case AMOUNT -> candidates.stream()		//check if the beam can be rendered by the amount defined by the player (via "/beam view")
					.sorted(Comparator.comparingDouble(Candidate::distance))
					.limit(BeaconBeamViewConfig.getAmount())
					.toList();
			case UNLIMITED -> candidates;
		};

		LocalPlayer player = client.player;

		for (Candidate candidate : toRender) {
			BlockPos pos = candidate.pos();
			float distanceToBeacon = (float) candidate.distance();

			// Had to make the beam bigger when the player is far away form the beacon
			// if I didn't do that, it simply wouldn't be possible to see him. (I wasted 2 hours trying to figure out
			//why i coulndt see the beam when I was 500 blocks away hahaha)

			float beamRadiusScale = player != null && player.isScoping() ? 1.0F : Math.max(1.0F, distanceToBeacon / 96.0F);

			poseStack.pushPose();
			poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());

			BeaconRenderer.submitBeaconBeam(
					poseStack,
					submitNodeStorage,
					BeaconRenderer.BEAM_LOCATION,
					1.0F,
					animationTime,
					0,
					BeaconRenderer.MAX_RENDER_Y,
					0xFFFFFFFF,
					BeaconRenderer.SOLID_BEAM_RADIUS * beamRadiusScale,
					BeaconRenderer.BEAM_GLOW_RADIUS * beamRadiusScale
			);

			poseStack.popPose();
		}
	}

	private record Candidate(BlockPos pos, double distance) {
	}
}