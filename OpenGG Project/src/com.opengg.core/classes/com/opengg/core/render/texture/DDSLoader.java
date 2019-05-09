package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.system.Allocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class DDSLoader {
    private static final int DXT1 = (0x44585431);
    private static final int DXT2 = (0x44585432);
    private static final int DXT3 = (0x44585433);
    private static final int DXT4 = (0x44585434);
    private static final int DXT5 = (0x44585435);
    public static TextureData load(String path){
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
            //GGConsole.debug(width+","+height+","+linearSize+","+mipMapCount);
            int bufferSize = mipMapCount > 1 ? linearSize * 2:linearSize;
            ByteBuffer b = Allocator.alloc(bufferSize);
            while(b.hasRemaining())fc.getChannel().read(b);
            int numComponents = (fourCC == DXT1)?3:4;
            b.flip();
            //System.out.println(fourCC+","+DXT1+","+DXT3+","+DXT5);
            TextureData data = new TextureData(width,height,numComponents,b,path,switch(fourCC){
                case DXT1 -> TextureData.TType.DXT1; case DXT3 -> TextureData.TType.DXT3;case DXT5 -> TextureData.TType.DXT5;default->TextureData.TType.DXT1;
            });
            data.setMipMapCount(mipMapCount);
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
