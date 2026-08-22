package com.beacon.beam;

import com.beacon.beam.network.ModNetworking;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeamInfinityViewDistance implements ModInitializer {
	public static final String MOD_ID = "beam-infinity-view-distance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModNetworking.registerCommon();
		BeaconBeamServerEvents.register();
		LOGGER.info("FAB is online. OlafBRGAMER_ says hi! (hope i see u in the future)");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
