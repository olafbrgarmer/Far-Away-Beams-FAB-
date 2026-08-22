package com.beacon.beam.client;

import net.fabricmc.api.ClientModInitializer;

public class BeamInfinityViewDistanceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {			//I think this is important
		BeaconBeamClientNetworking.register();
		BeaconBeamClientEvents.register();
		BeaconBeamCommands.register();
	}
}