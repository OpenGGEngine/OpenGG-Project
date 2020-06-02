package com.opengg.core.network;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.network.common.ReceivingBulkMessage;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.network.common.SendingBulkMessage;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BulkNetworkDataManager {
    public final int maxAllowedBandwidth = 1_048_576;
    public final int maxPacketSize = 32_768;
    public final float perPacketTime = (float)maxPacketSize/(float)maxAllowedBandwidth;
    private Map<Tuple<ConnectionData, Long>, ReceivingBulkMessage> currentReceivedMessages = new HashMap<>();
    private List<Tuple<SendingBulkMessage, CompletableFuture<Boolean>>> currentSendingMessages = new ArrayList<>();
    private List<Consumer<ReceivingBulkMessage>> messageListeners = new ArrayList<>();

    private float counter = 0;

    public BulkNetworkDataManager(){
        NetworkEngine.getPacketReceiver().addProcessor(PacketType.BULK_DATA_INIT, (p) -> {
            try {
                var message = new ReceivingBulkMessage(p);
                GGConsole.debug("Receiving bulk message from " + p.getConnection() + ", " + message.getSize() + " bytes (" + message.getPacketAmount() + " chunks)");
                currentReceivedMessages.put(Tuple.of(message.getSource(), message.getId()), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        NetworkEngine.getPacketReceiver().addProcessor(PacketType.BULK_DATA_CHUNK, (p) -> {
            try {
                long id = new GGInputStream(p.getData()).readLong();
                if(!currentReceivedMessages.containsKey(Tuple.of(p.getConnection(), id))){
                    GGConsole.warning("Received bulk packet block for unknown bulk data pack!");
                    return;
                }
                var bulkMessage = currentReceivedMessages.get(Tuple.of(p.getConnection(), id));
                bulkMessage.acceptPacket(p);
                if(bulkMessage.isComplete()){
                    currentReceivedMessages.remove(Tuple.of(p.getConnection(), id));
                    messageListeners.stream().forEach(c -> c.accept(bulkMessage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.onMessageArrival(c -> {
            if(c.isFile()){
                var dir = Resource.getUserDataPath() + "downloads" + File.separator + c.getPath();
                var file = Paths.get(dir);
                file.getParent().toFile().mkdirs();
                try {
                    var out = new GGOutputStream(new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE)));
                    out.write(c.getAllData());
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void update(float delta){
        counter += delta;
        if(counter > perPacketTime){
            counter = 0;
            var done = currentSendingMessages.stream()
                    .peek(c -> c.x().sendNextPacket())
                    .filter(c -> c.x().isComplete())
                    .collect(Collectors.toList());
            done.forEach(c -> c.y().complete(true));
            currentSendingMessages.removeAll(done);
        }
    }

    public CompletableFuture<Boolean> send(String file, String name, ConnectionData target){
        try {
            var data = Files.readAllBytes(Paths.get(Resource.getAbsoluteFromLocal(file)));
            var sendingList = new SendingBulkMessage(data, name, file, target);
            var future = new CompletableFuture<Boolean>();

            currentSendingMessages.add(Tuple.of(sendingList, future));
            sendingList.sendNextPacket();
            return future;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    public CompletableFuture<Boolean> send(byte[] data, String name, ConnectionData target){
        var sendingList = new SendingBulkMessage(data, name, target);
        var future = new CompletableFuture<Boolean>();
        currentSendingMessages.add(Tuple.of(sendingList, future));
        sendingList.sendNextPacket();
        return future;
    }

    public void onMessageArrival(Consumer<ReceivingBulkMessage> consumer){
        this.messageListeners.add(consumer);
    }

}
