package com.opengg.core.render.text;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.Text;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.Allocator;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.opengg.core.util.FileUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TTF {

    private final ByteBuffer ttf;
    private final STBTTFontinfo fontinfo;
    //For Oversampled Option
    private static final float[] scale = {
            24.0f,
            1.5f
    };
    private final int ascent;
    private final int descent;
    private final int lineGap;
    private final int fontheight = 14;
    public Texture texture;

    private final int WIDTH = 1024;
    private final int HEIGHT = 1024;
    private STBTTBakedChar.Buffer cdata;
    private STBTTPackedchar.Buffer altCData;

    private boolean kerning = false;
    private boolean isOversampled = false;

    public static TTF getTruetypeFont(String path){ return getTruetypeFont(path,false); }
    public static TTF getTruetypeFont(String path,boolean oversample){
        return new TTF(path,oversample);
    }

    private TTF(String path,boolean isOversampled){
        this.isOversampled = isOversampled;
        try {
            ttf = ioResourceToByteBuffer(path, 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fontinfo = STBTTFontinfo.create();

        if (!stbtt_InitFont(fontinfo, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        IntBuffer pAscent  = Allocator.stackAllocInt(1);
        IntBuffer pDescent = Allocator.stackAllocInt(1);
        IntBuffer pLineGap = Allocator.stackAllocInt(1);

        stbtt_GetFontVMetrics(fontinfo, pAscent, pDescent, pLineGap);

        ascent = pAscent.get(0);
        descent = pDescent.get(0);
        lineGap = pLineGap.get(0);

        Allocator.popStack();
        Allocator.popStack();
        Allocator.popStack();
        if(isOversampled) {
            altCData = overSampleInit(WIDTH,HEIGHT);
        }else{
            cdata = init(WIDTH, HEIGHT);
        }
    }

    private STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
        ByteBuffer bitmap = Allocator.alloc(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontheight, bitmap, BITMAP_W, BITMAP_H, 32, cdata);


        ByteBuffer realmap = Allocator.alloc(BITMAP_W * BITMAP_H * 4);
        for (int i = 0; i < BITMAP_H * BITMAP_W; i++) {
            realmap.put((byte) 0xFF).put((byte) 0xFF).put((byte) 0xFF).put(bitmap.get());
        }

        realmap.flip();

        TextureData data = new TextureData(BITMAP_W, BITMAP_H, 4, realmap, "internal");

        texture = Texture.create(Texture.config(), data);
        return cdata;
    }

    private STBTTPackedchar.Buffer overSampleInit(int BITMAP_W, int BITMAP_H) {
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.malloc(96);
        STBTTPackContext pc = STBTTPackContext.malloc();

        ByteBuffer bitmap = Allocator.alloc(BITMAP_W * BITMAP_H);

        stbtt_PackBegin(pc,bitmap,BITMAP_W,BITMAP_H,0,1,NULL);

        for (int i = 0; i < 2; i++) {
            int p = (i * 3 + 0) * 128 + 32;
            cdata.limit(p + 95);
            cdata.position(p);
            stbtt_PackSetOversampling(pc, 1, 1);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, cdata);

            p = (i * 3 + 1) * 128 + 32;
            cdata.limit(p + 95);
            cdata.position(p);
            stbtt_PackSetOversampling(pc, 2, 2);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, cdata);

            p = (i * 3 + 2) * 128 + 32;
            cdata.limit(p + 95);
            cdata.position(p);
            stbtt_PackSetOversampling(pc, 3, 1);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, cdata);
        }
        cdata.clear();
        stbtt_PackEnd(pc);


        ByteBuffer realmap = Allocator.alloc(BITMAP_W * BITMAP_H * 4);
        for (int i = 0; i < BITMAP_H * BITMAP_W; i++) {
            realmap.put((byte) 0xFF).put((byte) 0xFF).put((byte) 0xFF).put(bitmap.get());
        }

        realmap.flip();

        TextureData data = new TextureData(BITMAP_W, BITMAP_H, 4, realmap, "internal");

        texture = Texture.create(Texture.config(), data);
        return cdata;
    }


    public Drawable useOn(String text){
        float scale = stbtt_ScaleForPixelHeight(fontinfo, fontheight);

        IntBuffer pCodePoint = Allocator.stackAllocInt(1);

        FloatBuffer x = Allocator.stackAllocFloat(1);
        FloatBuffer y = Allocator.stackAllocFloat(1);

        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack();

        int lineStart = 0;

        float factorX = 1.5f;
        float factorY = 1.5f;

        float lineY = 0.0f;

        List<Vector2f> uvs = new ArrayList(text.length()*4);
        List<Vector2f> poss = new ArrayList(text.length()*4);

        for (int i = 0, to = text.length(); i < to; ) {
            i += getCP(text, to, i, pCodePoint);

            int cp = pCodePoint.get(0);
            if (cp == '\n') {
                //if (isLineBBEnabled()) {
                //    glEnd();
                //    renderLineBB(lineStart, i - 1, y.get(0), scale);
                //    glBegin(GL_QUADS);
                //}

                y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                x.put(0, 0.0f);

                lineStart = i;
                continue;
            } else if (cp < 32 || 128 <= cp) {
                continue;
            }

            float cpX = x.get(0);
            if(!isOversampled) {
                stbtt_GetBakedQuad(cdata, WIDTH, HEIGHT, cp - 32, x, y, q, true);
            }else {
                stbtt_GetPackedQuad(altCData, WIDTH, HEIGHT, text.charAt(i), x, y, q, false);
            }
            x.put(0, scale(cpX, x.get(0), factorX));
            if (isKerningEnabled() && i < to) {
                getCP(text, to, i, pCodePoint);
                x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontinfo, cp, pCodePoint.get(0)) * scale);
            }

            float
                    x0 = scale(cpX, q.x0(), factorX),
                    x1 = scale(cpX, q.x1(), factorX),
                    y0 = scale(lineY, q.y0(), factorY),
                    y1 = scale(lineY, q.y1(), factorY);
/*
            glTexCoord2f(q.s0(), q.t0());
            glVertex2f(x0, y0);

            glTexCoord2f(q.s1(), q.t0());
            glVertex2f(x1, y0);

            glTexCoord2f(q.s1(), q.t1());
            glVertex2f(x1, y1);

            glTexCoord2f(q.s0(), q.t1());
            glVertex2f(x0, y1);*/
            uvs.add(new Vector2f(q.s0(), q.t0()));
            uvs.add(new Vector2f(q.s1(), q.t0()));
            uvs.add(new Vector2f(q.s1(), q.t1()));

            uvs.add(new Vector2f(q.s1(), q.t1()));
            uvs.add(new Vector2f(q.s0(), q.t1()));
            uvs.add(new Vector2f(q.s0(), q.t0()));

            poss.add(new Vector2f(x0, y0));
            poss.add(new Vector2f(x1, y0));
            poss.add(new Vector2f(x1, y1));

            poss.add(new Vector2f(x1, y1));
            poss.add(new Vector2f(x0, y1));
            poss.add(new Vector2f(x0, y0));
        }

        Allocator.popStack();
        Allocator.popStack();
        Allocator.popStack();

        FloatBuffer data = Allocator.allocFloat(poss.size()*12);

        for(int i = 0; i < poss.size(); i++){
            var uv = uvs.get(i);
            var pos = poss.get(i);
            data.put(pos.x).put(-pos.y).put(0).put(1).put(0).put(0).put(1).put(1f).put(0f).put(0f).put(uv.x).put(uv.y);
        }

        data.flip();

        return new TexturedDrawnObject(new DrawnObject(data), this.texture);
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    public boolean isKerningEnabled() {
        return kerning;
    }

    public void setKerning(boolean kerning) {
        this.kerning = kerning;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }
}
