package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.system.Allocator;

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
            Allocator.popStack();
            int bufferSize = mipMapCount > 1 ? linearSize * 2:linearSize;
            //ByteBuffer b = Allocator.alloc(bufferSize);
            //while(b.hasRemaining())fc.getChannel().read(b);
            ByteBuffer b = ByteBuffer.wrap(fc.readNBytes(bufferSize));
            //fc.getChannel().read(b);
            int numComponents = (fourCC == DXT1)?3:4;
            //System.out.println(fourCC+","+DXT1+","+DXT3+","+DXT5);
            TextureData data = new TextureData(width,height,numComponents,b,path,switch(fourCC){
                case DXT1 -> TextureData.TextureDataType.DXT1; case DXT3 -> TextureData.TextureDataType.DXT3;case DXT5 -> TextureData.TextureDataType.DXT5;default-> TextureData.TextureDataType.DXT1;
            });
            data.setMipMapCount(mipMapCount);
            return data;
        }
    }
}
