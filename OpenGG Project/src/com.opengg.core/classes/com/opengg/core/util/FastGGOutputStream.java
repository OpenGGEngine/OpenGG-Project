package com.opengg.core.util;

import java.io.IOException;

public class FastGGOutputStream extends GGOutputStream {
    public void write(String s) throws IOException {
        write(s.length());
        write(s.getBytes());
    }
    public void writeInt(int v) throws IOException {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>>  8) & 0xFF);
        write((v >>>  0) & 0xFF);
    }
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }
}
