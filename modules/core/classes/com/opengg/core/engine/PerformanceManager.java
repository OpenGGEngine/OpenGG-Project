package com.opengg.core.engine;

import com.opengg.core.math.FastInt;
import com.opengg.core.math.util.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PerformanceManager {
    private static double computedFramerate = 0.1f;
    private static double computedDrawCalls = 0.1f;
    private static long networkBytesInSec = 0;
    private static long networkBytesOutSec = 0;

    private static long bufferAllocsThisFrame = 0;
    private static long bufferAllocSizeThisFrame = 0;

    private static long descriptorSetAllocations = 0;
    private static final long descriptorCacheHits = 0;

    private static int packetsInSec = 0;
    private static int packetsOutSec = 0;
    private static int packetsDroppedSec = 0;
    private static int ackPacketsOutSec;

    private static final Queue<Float> lastFrames = new LinkedList<>();
    private static final LinkedList<FastInt> lastDrawCalls = new LinkedList<>(List.of(new FastInt()));
    private static final List<Tuple<Long, Integer>> bytesOut = new ArrayList<>();
    private static final List<Tuple<Long, Integer>> bytesIn = new ArrayList<>();

    private static final List<Long> packetsIn = new ArrayList<>();
    private static final List<Long> packetsOut = new ArrayList<>();
    private static final List<Long> ackPacketsOut = new ArrayList<>();
    private static final List<Long> packetsDropped = new ArrayList<>();

    private static List<Long> gpuBuffers = new ArrayList<>();
    private static List<Long> descriptorSets = new ArrayList<>();


    public static void update(float delta){
        lastFrames.offer(delta);
        if (lastFrames.size() > 15){
            computedFramerate = lastFrames.stream().mapToDouble(i -> i).average().getAsDouble();
            lastFrames.poll();
        }

        lastDrawCalls.offer(new FastInt());
        if (lastDrawCalls.size() > 15){
            computedDrawCalls = lastDrawCalls.stream().mapToLong(FastInt::get).average().getAsDouble();
            lastDrawCalls.poll();
        }
        bytesOut.removeIf(t -> t.x() < System.currentTimeMillis() - 1000);
        bytesIn.removeIf(t -> t.x() < System.currentTimeMillis() - 1000);

        packetsOut.removeIf(t -> t < System.currentTimeMillis() - 1000);
        ackPacketsOut.removeIf(t -> t < System.currentTimeMillis() - 1000);
        packetsIn.removeIf(t -> t < System.currentTimeMillis() - 1000);
        packetsDropped.removeIf(t -> t < System.currentTimeMillis() - 1000);

        networkBytesInSec = bytesIn.stream().mapToLong(Tuple::y).sum();
        networkBytesOutSec = bytesOut.stream().mapToLong(Tuple::y).sum();

        bufferAllocsThisFrame = gpuBuffers.size();
        bufferAllocSizeThisFrame = gpuBuffers.stream().mapToLong(i -> i).sum();

        descriptorSetAllocations = descriptorSets.size();

        packetsInSec = packetsIn.size();
        packetsOutSec = packetsOut.size();
        ackPacketsOutSec = ackPacketsOut.size();
        packetsDroppedSec = packetsDropped.size();

        gpuBuffers = new ArrayList<>();
        descriptorSets = new ArrayList<>();
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
        OpenGG.asyncExec(() -> ackPacketsOut.add(System.currentTimeMillis()));
    }

    public static void registerPacketResent(){
        OpenGG.asyncExec(() -> packetsDropped.add(System.currentTimeMillis()));
    }

    public static void registerBufferAllocation(long size){
        gpuBuffers.add(size);
    }

    public static void registerDrawCall(){
        lastDrawCalls.get(lastDrawCalls.size()-1).set(lastDrawCalls.get(lastDrawCalls.size()-1).get()+1);
    }

    public static void registerDescriptorSet(){
        descriptorSets.add(0L);
    }

    public static double getComputedFrameTime() {
        return computedFramerate;
    }

    public static double getComputedDrawCalls() {
        return computedDrawCalls;
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

    public static long getBufferAllocsThisFrame() {
        return bufferAllocsThisFrame;
    }

    public static long getBufferAllocSizeThisFrame() {
        return bufferAllocSizeThisFrame;
    }

    public static long getDescriptorSetAllocations() {
        return descriptorSetAllocations;
    }

    public static int getAckPacketsOutSec() {
        return ackPacketsOutSec != 0 ? ackPacketsOutSec : 1;
    }
}
