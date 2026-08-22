package com.beacon.beam.client;

import com.beacon.beam.BeaconBeamData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BeaconBeamClientRegistry {

    private static final Set<BeaconBeamData> activeBeacons = new HashSet<>();

    private BeaconBeamClientRegistry() {
    }

    public static Set<BeaconBeamData> getAll() {
        return Collections.unmodifiableSet(activeBeacons);
    }

    public static void replaceAll(Collection<BeaconBeamData> beacons) {
        activeBeacons.clear();
        activeBeacons.addAll(beacons);
    }

    public static void activate(BeaconBeamData beacon) {
        activeBeacons.add(beacon);
    }

    public static void deactivate(BeaconBeamData beacon) {
        activeBeacons.remove(beacon);
    }

    public static void clear() {
        activeBeacons.clear();
    }
}