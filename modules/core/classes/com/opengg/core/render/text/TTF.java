package com.opengg.core.render.text;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.Allocator;
import org.lwjgl.stb.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.opengg.core.util.FileUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TTF implements Font{
    static HashMap<String, TTF> cache = new HashMap<>();

    private final ByteBuffer ttf;
    private final STBTTFontinfo fontinfo;
    //For Oversampled Option
    private static final float[] scale = {
            32f
    };
    private final int ascent;
    private final int descent;
    private final int lineGap;
    private final int fontheight = 12;
    public Texture texture;

    private final int WIDTH = 1024;
    private final int HEIGHT = 1024;
    private STBTTBakedChar.Buffer cdata;
    private STBTTPackedchar.Buffer altCData;

    private boolean isOversampled = false;

    public static TTF getTruetypeFont(String path){ return getTruetypeFont(path,false); }
    public static TTF getTruetypeFont(String path, boolean oversample){
        if(cache.containsKey(path)){
            return cache.get(path);
        }else{
            var ttf = new TTF(path,oversample);
            cache.put(path, ttf);
            return ttf;
        }

    }

    private TTF(String path, boolean isOversampled){
        this.isOversampled = isOversampled;
        try {
            ttf = ioResourceToByteBuffer(path, 512 * 1024);
        } catch (Exception e) {
            GGConsole.warning("Failed to load font at " + path);
            throw new RuntimeException(e);
        }

        fontinfo = STBTTFontinfo.create();

        if (!stbtt_InitFont(fontinfo, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        IntBuffer pAscent = Allocator.stackAllocInt(1);
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

    private STBTTPackedchar.Buffer overSampleInit(int BITMAP_W, int BITMAP_H) {
        STBTTPackedchar.Buffer altCData = STBTTPackedchar.calloc(96 * 3 * scale.length + 128);
        STBTTPackContext pc = STBTTPackContext.calloc();

        ByteBuffer bitmap = Allocator.alloc(BITMAP_W * BITMAP_H);

        stbtt_PackBegin(pc,bitmap,BITMAP_W,BITMAP_H,0,1,NULL);

        for (int i = 0; i < scale.length; i++) {
            int p = (i * 3 + 0) * 128 + 32;
            altCData.limit(p + 95);
            altCData.position(p);
            stbtt_PackSetOversampling(pc, 1, 1);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, altCData);

            p = (i * 3 + 1) * 128 + 32;
            altCData.limit(p + 95);
            altCData.position(p);
            stbtt_PackSetOversampling(pc, 2, 2);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, altCData);

            p = (i * 3 + 2) * 128 + 32;
            altCData.limit(p + 95);
            altCData.position(p);
            stbtt_PackSetOversampling(pc, 3, 1);
            stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, altCData);
        }
        altCData.clear();
        stbtt_PackEnd(pc);

        texture = getTextureFrom(bitmap, BITMAP_W, BITMAP_H, new Vector3f(1,1,1));

        return altCData;
    }

    private STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.calloc(96);
        ByteBuffer bitmap = Allocator.alloc(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontheight, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

        texture = getTextureFrom(bitmap, BITMAP_W, BITMAP_H, new Vector3f(1,1,1));

        return cdata;
    }

    private static Texture getTextureFrom(ByteBuffer bitmap, int BITMAP_W, int BITMAP_H, Vector3f color){
        ByteBuffer realmap = Allocator.alloc(BITMAP_W * BITMAP_H * 4);
        for (int i = 0; i < BITMAP_H * BITMAP_W; i++) {
            realmap.put((byte) (color.x * 255)).put((byte) (color.y * 255)).put((byte) (color.z * 255)).put(bitmap.get());
        }
        realmap.flip();

        TextureData data = new TextureData(BITMAP_W, BITMAP_H, 4, realmap, "generated:font");
        var texture = Texture.create(Texture.config(), data);

        return texture;
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

    @Override
    public Renderable createFromText(String text) {
        return createFromText(Text.from(text));
    }

    @Override
    public Renderable createFromText(Text wholetext) {
        return createFromTextWithSize(wholetext, new Vector3fm());
    }

    @Override
    public Renderable createFromTextWithSize(Text wholetext, Vector3fm endSize) {
        String text = wholetext.getText();
        if(text.length() == 0){
            return ObjectCreator.createQuadPrism(new Vector3f(), new Vector3f());
        }

        IntBuffer pCodePoint = Allocator.stackAllocInt(1);

        FloatBuffer x = Allocator.stackAllocFloat(1);
        FloatBuffer y = Allocator.stackAllocFloat(1);

        STBTTAlignedQuad q = STBTTAlignedQuad.callocStack();

        float x0 = 0, x1 = 0, y0 = 0, y1 = 0;

        float scale = stbtt_ScaleForPixelHeight(this.fontinfo,12f);
        float factorX = wholetext.getSize()*scale;
        float factorY = wholetext.getSize()*scale;

        float lineY = 0.0f;

        List<Vector2f> uvs = new ArrayList<>(text.length()*4);
        List<Vector2f> poss = new ArrayList<>(text.length()*4);
        for (int i = 0, to = text.length(); i < to; ) {
            i += getCP(text, to, i, pCodePoint);

            int cp = pCodePoint.get(0);
            if (cp == '\n' || (x1 > wholetext.getMaxLineSize() && wholetext.getMaxLineSize() > 0f)) {
                lineY = lineY + (ascent - descent + lineGap) * scale * 2 + wholetext.getLinePadding();
                y.put(0, lineY);
                x.put(0, 0.0f);
                x1 = 0;
                continue;
            } else if (cp < 32 || 128 <= cp) {
                continue;
            }

            if(!isOversampled) {
                stbtt_GetBakedQuad(cdata, WIDTH, HEIGHT, cp, x, y, q, true);
            }else {
                stbtt_GetPackedQuad(altCData, WIDTH, HEIGHT, cp, x, y, q, false);
            }

            if (wholetext.isKerningEnabled() && i < to) {
                getCP(text, to, i, pCodePoint);
                x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontinfo, cp, pCodePoint.get(0)) * scale);
            }

            x0 = q.x0() * factorX;
            x1 = q.x1() * factorX;
            y0 = q.y0() * factorY;
            y1 = q.y1() * factorY;

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

            if(x1 > endSize.x) endSize.x = x1;
            if(y1 > endSize.y) endSize.y = y1+wholetext.getSize()*0.2f;
        }

        Allocator.popStack();
        Allocator.popStack();
        Allocator.popStack();

        FloatBuffer data = Allocator.allocFloat(poss.size()*8);

        for(int i = poss.size()-1; i >= 0; i--){
            var uv = uvs.get(i);
            var pos = poss.get(i);

            data.put(pos.x).put(-pos.y-wholetext.getSize()*0.2f).put(0).put(1f).put(0f).put(0f).put(uv.x).put(uv.y);
        }
        data.flip();

        var object = DrawnObject.create(data);
        return () -> {
            ShaderController.useConfiguration("ttf");
            ShaderController.setUniform("color", wholetext.getColor());
            ShaderController.setUniform("Kd", texture);
            object.render();
            ShaderController.useConfiguration("object");

        };
    }
}