package com.beacon.beam;

import com.beacon.beam.network.BeaconBeamSyncPayload;
import com.beacon.beam.network.BeaconBeamUpdatePayload;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BeaconBeamRegistry {

    private static final String SAVE_FILE_NAME = BeamInfinityViewDistance.MOD_ID + "/active_beacons.json";

    private static final Set<BeaconBeamData> activeBeacons = new HashSet<>();

    private BeaconBeamRegistry() {          //Useless HAHA (like me)
    }

    public static Set<BeaconBeamData> getAll() {            //Useless AGAIN HAHAHAHAHA
        return Collections.unmodifiableSet(activeBeacons);
    }

    public static void activate(MinecraftServer server, BeaconBeamData beacon) {
        if (activeBeacons.add(beacon)) {
            save(server);
            broadcast(server, new BeaconBeamUpdatePayload(beacon, true));
        }
    }

    public static void deactivate(MinecraftServer server, BeaconBeamData beacon) {
        if (activeBeacons.remove(beacon)) {
            save(server);
            broadcast(server, new BeaconBeamUpdatePayload(beacon, false));
        }
    }

    public static void sendFullSync(ServerPlayer player) {
        ServerPlayNetworking.send(player, new BeaconBeamSyncPayload(new HashSet<>(activeBeacons).stream().toList()));
    }

    //NEVER CHANGING THAT, this update only when the beacon is updated, not an update for every single tick (its good to prevent lag)
    private static void broadcast(MinecraftServer server, BeaconBeamUpdatePayload payload) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            if (ServerPlayNetworking.canSend(player, BeaconBeamUpdatePayload.TYPE)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    // --- Persistence from JSON to my worlds/servers starts here ------------

    private static Path getSavePath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(SAVE_FILE_NAME);
    }

    public static void load(MinecraftServer server) {
        activeBeacons.clear();

        Path path = getSavePath(server);
        if (!Files.exists(path)) {
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

                activeBeacons.add(new BeaconBeamData(dimension, pos));
            }
        } catch (IOException e) {
            BeamInfinityViewDistance.LOGGER.error("Failed to load active beacons from {}", path, e);
        }
    }

    public static void save(MinecraftServer server) {
        Path path = getSavePath(server);

        JsonArray root = new JsonArray();
        for (BeaconBeamData beacon : activeBeacons) {
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
            BeamInfinityViewDistance.LOGGER.error("Failed to save active beacons to {}", path, e);
        }
    }
}