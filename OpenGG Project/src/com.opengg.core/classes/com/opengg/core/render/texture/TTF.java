package com.opengg.core.render.texture;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.system.Allocator;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.opengg.core.util.FileUtil.ioResourceToByteBuffer;
import static org.lwjgl.stb.STBTruetype.*;

public abstract class TTF implements Font{
    protected ByteBuffer ttf;
    protected STBTTFontinfo fontinfo;
    protected int ascent;
    protected int descent;
    protected int lineGap;
    protected int fontheight = 1;
    public Texture texture;

    protected final int WIDTH = 1024;
    protected final int HEIGHT = 1024;

    protected boolean kerning = false;

    public TTF(String path) {
        initializeFont(path);

        createTexture();
    }

    private void initializeFont(String path) {
        try {
            ttf = ioResourceToByteBuffer(path, 512 * 1024);
        } catch (IOException e) {
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
    }

    public abstract void createTexture();

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
    public Drawable createFromText(String text) {
        return createFromText(Text.from(text));
    }

    @Override
    public Drawable createFromText(Text wholetext) {
        String text = wholetext.getText();

        IntBuffer pCodePoint = Allocator.stackAllocInt(1);
        float scale = stbtt_ScaleForPixelHeight(this.fontinfo,28f);

        FloatBuffer x = Allocator.stackAllocFloat(1);
        FloatBuffer y = Allocator.stackAllocFloat(1);

        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack();

        int lineStart = 0;

        float x0 = 0, x1 = 0, y0 = 0, y1 = 0;

        float factorX = wholetext.getSize()*scale;
        float factorY = wholetext.getSize()*scale;

        float lineY = 0.0f;

        List<Vector2f> uvs = new ArrayList(text.length()*4);
        List<Vector2f> poss = new ArrayList(text.length()*4);

        for (int i = 0, to = text.length(); i < to; ) {
            i += getCP(text, to, i, pCodePoint);

            int cp = pCodePoint.get(0);
            if (cp == '\n' || (x1 > wholetext.getMaxLineSize() && wholetext.getMaxLineSize() > 0f)) {
                //if (isLineBBEnabled()) {
                //    glEnd();
                //    renderLineBB(lineStart, i - 1, y.get(0), scale);
                //    glBegin(GL_QUADS);
                //}

                y.put(0, lineY = y.get(0) + ((ascent - descent + lineGap) * scale * factorY));
                x.put(0, 0.0f);

                lineStart = i;
                if(cp == '\n')
                    continue;
            } else if (cp < 32 || 128 <= cp) {
                continue;
            }

            float cpX = x.get(0);

            q = getQuad(x,y,q,cp);

            x.put(0, scale(cpX, x.get(0), factorX));
            if (wholetext.isKerningEnabled() && i < to) {
                getCP(text, to, i, pCodePoint);
                x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontinfo, cp, pCodePoint.get(0)) * scale * factorX);
            }


            x0 = scale(cpX, q.x0(), factorX);
            x1 = scale(cpX, q.x1(), factorX);
            y0 = scale(lineY, q.y0(), factorY);
            y1 = scale(lineY, q.y1(), factorY);

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

    public abstract STBTTAlignedQuad getQuad(FloatBuffer x, FloatBuffer y, STBTTAlignedQuad q, int cp) ;
}
