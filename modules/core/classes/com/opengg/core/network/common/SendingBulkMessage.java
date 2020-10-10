package com.opengg.core.network.common;

import com.opengg.core.network.NetworkEngine;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.util.HashUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SendingBulkMessage {
    private final ConnectionData target;
    private final String name;
    private final String path;
    private final boolean isFile;
    private final long id;
    private final int size;
    private final long hash;
    private final int packetAmount;
    private final List<BulkMessageSection> data;

    private boolean beganSending = false;

    public SendingBulkMessage(byte[] data, String name, ConnectionData target){
        this(data, name, null, target);
    }

    public SendingBulkMessage(byte[] data, String name, String file, ConnectionData target){
        this.name = name;
        if(file != null){
            isFile = true;
            path = file;
        }else{
            isFile = false;
            path = "";
        }

        size = data.length;
        packetAmount = (int) Math.ceil(data.length / (float)  NetworkEngine.getBulkNetworkDataManager().maxPacketSize);
        id = UUID.randomUUID().getLeastSignificantBits();
        this.target = target;
        this.hash = HashUtil.getMeowHash(data);
        this.data = this.split(data);
    }

    public void sendNextPacket(){
        if(!beganSending){
            try {
                var out = new GGOutputStream();
                out.write(id);
                out.write(size);
                out.write(packetAmount);
                out.write(isFile);
                out.write(hash);
                out.write(name);
                if(isFile)
                    out.write(path);

                Packet.sendGuaranteed(PacketType.BULK_DATA_INIT, out.asByteArray(), target);

                beganSending = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            var nextSentOpt = data.stream()
                    .filter(t -> !t.sent)
                    .findFirst();
            if(nextSentOpt.isEmpty()){
                return;
            }

            var nextSent = nextSentOpt.get();
            nextSent.send();
            nextSent.sent = true;
        }
    }

    public boolean isComplete() {
        return data.stream().allMatch(t -> t.sent);
    }

    private List<BulkMessageSection> split(byte[] data){
        var sections = new ArrayList<BulkMessageSection>();
        for (int i = 0; i < packetAmount; i++) {
            int startIndex = i * NetworkEngine.getBulkNetworkDataManager().maxPacketSize;
            int endIndex;
            if(i == packetAmount - 1)
                endIndex = data.length;
            else
                endIndex = (i + 1) *  NetworkEngine.getBulkNetworkDataManager().maxPacketSize;

            var message = new BulkMessageSection();
            message.sectionId = i;
            message.length = endIndex-startIndex;
            message.data = Arrays.copyOfRange(data, startIndex, endIndex);
            sections.add(message);
        }
        return sections;
    }

    private class BulkMessageSection{
        int sectionId;
        int length;
        byte[] data;
        public boolean sent;

        void send(){
            try {
                var out = new GGOutputStream();
                out.write(id);
                out.write(sectionId);
                out.write(length);
                out.write(data);

                Packet.sendGuaranteed(PacketType.BULK_DATA_CHUNK, out.asByteArray(), target);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
