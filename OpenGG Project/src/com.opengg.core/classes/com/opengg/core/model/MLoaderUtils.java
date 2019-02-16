package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MLoaderUtils {
    public static void storeMat4(Matrix4f mat, ByteBuffer buf){
        buf.putFloat(mat.m00).putFloat(mat.m01).putFloat(mat.m02).putFloat(mat.m03);
        buf.putFloat(mat.m10).putFloat(mat.m11).putFloat(mat.m12).putFloat(mat.m13);
        buf.putFloat(mat.m20).putFloat(mat.m21).putFloat(mat.m22).putFloat(mat.m23);
        buf.putFloat(mat.m30).putFloat(mat.m31).putFloat(mat.m32).putFloat(mat.m33);
    }

    public static Matrix4f loadMat4(ByteBuffer buf){
        return new Matrix4f(buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),
                buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat(),buf.getFloat()
        ,buf.getFloat(),buf.getFloat(),buf.getFloat());
    }
    public static void writeString(String s, ByteBuffer b) {
        ByteBuffer temp = ByteBuffer.wrap(s.getBytes(Charset.forName("UTF-8")));
        b.putInt(temp.capacity());
        b.put(temp);
    }

    public static String readString(ByteBuffer b) {
        int namelength = b.getInt();
        if (namelength == 0) {
            return "";
        }
        byte[] name = new byte[namelength];
        b.get(name);
        return new String(name, Charset.forName("UTF-8"));
    }
}
