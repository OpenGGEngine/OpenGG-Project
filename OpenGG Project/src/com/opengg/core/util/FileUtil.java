/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Javier
 */
public class FileUtil {
    public static String getFileName(String path){
        int lio = path.lastIndexOf("/");
        if(lio < 0)
            lio = path.lastIndexOf("\\");
        return path.substring(lio, path.length()-4);
    }
     private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
}

/**
 * Reads the specified resource and returns the raw data as a ByteBuffer.
 *
 * @param resource   the resource to read
 * @param bufferSize the initial buffer size
 *
 * @return the resource data
 *
 * @throws IOException if an IO error occurs
 */
public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
    ByteBuffer buffer;

    Path path = Paths.get(resource);
    if ( Files.isReadable(path) ) {
        try (SeekableByteChannel fc = Files.newByteChannel(path)) {
            buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
            while ( fc.read(buffer) != -1 ) ;
        }
    } else {
        try (
            InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            ReadableByteChannel rbc = Channels.newChannel(source)
        ) {
            buffer = BufferUtils.createByteBuffer(bufferSize);

            while ( true ) {
                int bytes = rbc.read(buffer);
                if ( bytes == -1 )
                    break;
                if ( buffer.remaining() == 0 )
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
            }
        }
    }

    buffer.flip();
    return buffer;
}
}
