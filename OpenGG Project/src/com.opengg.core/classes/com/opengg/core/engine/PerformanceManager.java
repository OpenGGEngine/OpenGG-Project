package com.opengg.core.engine;

import com.opengg.core.math.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PerformanceManager {
    private static double computedFramerate = 0.1f;
    private static long networkBytesInSec = 0;
    private static long networkBytesOutSec = 0;

    private static int packetsInSec = 0;
    private static int packetsOutSec = 0;
    private static int packetsDroppedSec = 0;
    private static int ackPacketsOutSec;

    private static Queue<Float> lastFrames = new LinkedList<>();
    private static List<Tuple<Long, Integer>> bytesOut = new ArrayList<>();
    private static List<Tuple<Long, Integer>> bytesIn = new ArrayList<>();

    private static List<Long> packetsIn = new ArrayList<>();
    private static List<Long> packetsOut = new ArrayList<>();
    private static List<Long> ackPacketsOut = new ArrayList<>();
    private static List<Long> packetsDropped = new ArrayList<>();

    public static void update(float delta){
        lastFrames.offer(delta);
        if (lastFrames.size() > 15){
            computedFramerate = lastFrames.stream().mapToDouble(i -> i).average().getAsDouble();
            lastFrames.poll();
        }

        bytesOut.removeIf(t -> t.x < System.currentTimeMillis() - 1000);
        bytesIn.removeIf(t -> t.x < System.currentTimeMillis() - 1000);

        packetsOut.removeIf(t -> t < System.currentTimeMillis() - 1000);
        ackPacketsOut.removeIf(t -> t < System.currentTimeMillis() - 1000);
        packetsIn.removeIf(t -> t < System.currentTimeMillis() - 1000);
        packetsDropped.removeIf(t -> t < System.currentTimeMillis() - 1000);

        networkBytesInSec = bytesIn.stream().mapToLong(t -> t.y).sum();
        networkBytesOutSec = bytesOut.stream().mapToLong(t -> t.y).sum();

        packetsInSec = packetsIn.size();
        packetsOutSec = packetsOut.size();
        ackPacketsOutSec = ackPacketsOut.size();
        packetsDroppedSec = packetsDropped.size();

    }

    public static void registerPacketOut(int size){
        OpenGG.asyncExec(() -> {
            packetsOut.add(System.currentTimeMillis());
            bytesOut.add(Tuple.of(System.currentTimeMillis(), size));
        });
    }

    public static void registerPacketIn(int size){
        OpenGG.asyncExec(() -> {
            packetsIn.add(System.currentTimeMillis());
            bytesIn.add(Tuple.of(System.currentTimeMillis(), size));
        });
    }

    public static void registerGuaranteedPacketSent(){
        OpenGG.asyncExec(() -> {
            ackPacketsOut.add(System.currentTimeMillis());
        });
    }

    public static void registerPacketResent(){
        OpenGG.asyncExec(() -> {
            packetsDropped.add(System.currentTimeMillis());
        });
    }

    public static double getComputedFramerate() {
        return computedFramerate;
    }

    public static long getNetworkBytesInSec() {
        return networkBytesInSec;
    }

    public static long getNetworkBytesOutSec() {
        return networkBytesOutSec;
    }

    public static int getPacketsInSec() {
        return packetsInSec;
    }

    public static int getPacketsOutSec() {
        return packetsOutSec;
    }

    public static int getPacketsDroppedSec() {
        return packetsDroppedSec;
    }

    public static int getAckPacketsOutSec() {
        return ackPacketsOutSec != 0 ? ackPacketsOutSec : 1;
    }
}
