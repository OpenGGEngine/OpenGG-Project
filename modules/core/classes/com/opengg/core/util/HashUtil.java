package com.opengg.core.util;

import com.opengg.core.console.GGConsole;
import com.opengg.core.system.Allocator;
import org.lwjgl.util.meow.Meow;
import org.lwjgl.util.meow.MeowHash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HashUtil {
    public static long getMeowHash(String file){
        try {
            return getMeowHash(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            GGConsole.error("Failed to get hash for " + file);
        }
        return 0;
    }

    public static long getMeowHash(byte[] bytes) {
        return getMeowHash(Allocator.alloc(bytes.length).put(bytes).flip());
    }

    public static long getMeowHash(ByteBuffer buffer){
        MeowHash hash = MeowHash.create();
        Meow.MeowHash_Accelerated(0, buffer, hash);
        return hash.u64().get();
    }
}
