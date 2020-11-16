package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.system.Allocator;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DDSLoader {
    private static final int DXT1 = 0x31545844;//(0x44585431);
    private static final int DXT2 = 0x32545844;//(0x44585432);
    private static final int DXT3 = 0x33545844;//(0x44585433);
    private static final int DXT4 = 0x34545844;//(0x44585434);
    private static final int DXT5 = 0x35545844;//(0x44585435);
    public static TextureData loadFromBuffer(ByteBuffer b,String path){
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.position(12);
        int height = b.getInt();
        int width = b.getInt();
        int linearSize = b.getInt();
        b.position(28);
        int mipMapCount = b.getInt();
        b.position(84);
        int fourCC = b.getInt();
        int numComponents = (fourCC == DXT1)?3:4;
        b.position(128);
        System.out.println(width + ","+height+","+linearSize+","+mipMapCount);

        int bufferSize = mipMapCount > 1 ? linearSize * 2:linearSize;
        TextureData data = new TextureData(width, height, numComponents, MemoryUtil.memSlice(b,0,b.remaining()), path,
                switch(fourCC){
                    case DXT3 -> TextureData.TextureDataType.DXT3;
                    case DXT5 -> TextureData.TextureDataType.DXT5;
                    default-> TextureData.TextureDataType.DXT1;
                }
        );
        data.setMipMapCount(mipMapCount);
        return data;
    }
    public static TextureData load(String path) throws IOException {
        File f = new File(path);
        try(FileInputStream fc = new FileInputStream(f)){
            //Verify Header
            byte[] magic = new byte[4];
            byte [] header = new byte[124];
            fc.read(magic);
            if(!new String(magic).equals("DDS ")){
                GGConsole.error("Invalid DDS file.");
                return null;
            }
            fc.read(header);

            ByteBuffer tempBuf = Allocator.stackAlloc(124).put(header);
            int height = tempBuf.position(8).getInt();
            int width = tempBuf.position(12).getInt();
            int linearSize = tempBuf.position(16).getInt();
            int mipMapCount = tempBuf.position(24).getInt();
            int fourCC = tempBuf.position(80).getInt();
            int numComponents = (fourCC == DXT1)?3:4;
            Allocator.popStack();

            int bufferSize = mipMapCount > 1 ? linearSize * 2:linearSize;
            var readBytes = fc.readNBytes(bufferSize);
            ByteBuffer buffer = Allocator.alloc(readBytes.length).put(readBytes).rewind();

            TextureData data = new TextureData(width, height, numComponents, buffer, path,
                    switch(fourCC){
                        case DXT3 -> TextureData.TextureDataType.DXT3;
                        case DXT5 -> TextureData.TextureDataType.DXT5;
                        default-> TextureData.TextureDataType.DXT1;
                    }
            );
            data.setMipMapCount(mipMapCount);
            return data;
        }
    }
}
