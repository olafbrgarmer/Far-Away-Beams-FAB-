package com.beacon.beam.client;

import com.beacon.beam.BeaconBeamData;
import com.beacon.beam.BeamInfinityViewDistance;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BeaconBeamLocalDiscoveryRegistry {

    private static final Set<BeaconBeamData> knownBeacons = new HashSet<>();

    private static String activeServerIdentity = null;

    private BeaconBeamLocalDiscoveryRegistry() {
    }

    public static Set<BeaconBeamData> getAll() {
        return Collections.unmodifiableSet(knownBeacons);
    }

    public static void add(BeaconBeamData beacon) {
        if (knownBeacons.add(beacon)) {
            save();
        }
    }

    public static void remove(BeaconBeamData beacon) {
        if (knownBeacons.remove(beacon)) {
            save();
        }
    }

    public static void load(String serverIdentity) {
        activeServerIdentity = serverIdentity;
        knownBeacons.clear();

        Path path = getSavePath(serverIdentity);
        if (path == null || !Files.exists(path)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                return;
            }

            for (JsonElement element : root.getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();

                Identifier dimension = Identifier.tryParse(object.get("dimension").getAsString());
                if (dimension == null) {
                    continue;
                }

                BlockPos pos = new BlockPos(
                        object.get("x").getAsInt(),
                        object.get("y").getAsInt(),
                        object.get("z").getAsInt()
                );

                knownBeacons.add(new BeaconBeamData(dimension, pos));
            }
        } catch (IOException e) {
            BeamInfinityViewDistance.LOGGER.error("*Fail count = 23... Here we go again* Failed to LOAD file of discovered beacons from {}", path, e);
        }
    }

    public static void clear() {
        activeServerIdentity = null;
        knownBeacons.clear();
    }

    private static void save() {
        Path path = getSavePath(activeServerIdentity);
        if (path == null) {
            return;
        }

        JsonArray root = new JsonArray();
        for (BeaconBeamData beacon : knownBeacons) {
            JsonObject object = new JsonObject();
            object.addProperty("dimension", beacon.dimension().toString());
            object.addProperty("x", beacon.pos().getX());
            object.addProperty("y", beacon.pos().getY());
            object.addProperty("z", beacon.pos().getZ());
            root.add(object);
        }

        try {
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            }
        } catch (IOException e) {
            BeamInfinityViewDistance.LOGGER.error("*Fail counter = 2 (wow, just 2)* Failed to SAVE file of discovered beacons to {}", path, e);
        }
    }

    private static Path getSavePath(String serverIdentity) {
        if (serverIdentity == null) {
            return null;
        }

        String sanitized = serverIdentity.replaceAll("[^a-zA-Z0-9.\\-]", "_");

        return FabricLoader.getInstance().getConfigDir()
                .resolve(BeamInfinityViewDistance.MOD_ID)
                .resolve("known_beacons")
                .resolve(sanitized + ".json");
    }
}