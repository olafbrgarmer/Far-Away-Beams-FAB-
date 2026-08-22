package com.beacon.beam.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;

public final class BeaconBeamCommands {

    private BeaconBeamCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommands.literal("beam")
                        .then(ClientCommands.literal("view")
                                .executes(BeaconBeamCommands::showCurrent)
                                .then(ClientCommands.literal("clear")
                                        .executes(context -> {
                                            BeaconBeamViewConfig.clear();
                                            context.getSource().sendFeedback(Component.literal("Beam Infinity: limit removed."));
                                            return 1;
                                        }))
                                .then(ClientCommands.literal("radius")
                                        .then(ClientCommands.argument("blocks", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    int value = IntegerArgumentType.getInteger(context, "blocks");
                                                    BeaconBeamViewConfig.setRadius(value);
                                                    context.getSource().sendFeedback(Component.literal("Beam Infinity: Radius defined to " + value + " blocks."));
                                                    return 1;
                                                })))
                                .then(ClientCommands.literal("amount")
                                        .then(ClientCommands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    int value = IntegerArgumentType.getInteger(context, "amount");
                                                    BeaconBeamViewConfig.setAmount(value);
                                                    context.getSource().sendFeedback(Component.literal("Beam Infinity: showing only the " + value + " closest ones."));
                                                    return 1;
                                                }))))));
    }

    private static int showCurrent(com.mojang.brigadier.context.CommandContext<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> context) {
        String message = switch (BeaconBeamViewConfig.getMode()) {
            case UNLIMITED -> "Beam Infinity: No limit configured.";
            case RADIUS -> "Beam Infinity: Current radius is " + BeaconBeamViewConfig.getRadius() + " blocks (or " + BeaconBeamViewConfig.getRadius()/16 + " chunks).";
            case AMOUNT -> "Beam Infinity: Showing only the " + BeaconBeamViewConfig.getAmount() + " closest ones.";
        };
        context.getSource().sendFeedback(Component.literal(message));
        return 1;
    }
}