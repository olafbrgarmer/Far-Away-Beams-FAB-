package com.beacon.beam.client;

public final class BeaconBeamViewConfig {

    public enum Mode {
        UNLIMITED,
        RADIUS,
        AMOUNT
    }

    private static volatile Mode mode = Mode.UNLIMITED;
    private static volatile int radiusBlocks = 0;
    private static volatile int amount = 0;

    private BeaconBeamViewConfig() {        //Another useless code
    }

    public static Mode getMode() {
        return mode;
    }

    public static int getRadius() {
        return radiusBlocks;
    }

    public static int getAmount() {
        return amount;
    }

    public static void setRadius(int radiusBlocks) {
        BeaconBeamViewConfig.radiusBlocks = radiusBlocks;
        mode = Mode.RADIUS;
    }

    public static void setAmount(int amount) {
        BeaconBeamViewConfig.amount = amount;
        mode = Mode.AMOUNT;
    }

    public static void clear() {
        mode = Mode.UNLIMITED;
    }
}