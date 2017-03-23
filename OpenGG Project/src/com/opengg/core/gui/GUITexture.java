/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectBuffers;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Warren
 */
public class GUITexture extends GUIItem {

    private static DrawnObject dummy = null;

    public GUITexture(Texture g, Vector2f screenpos, Vector2f size) {

        super(dummy, screenpos);
        this.screenlocalpos = screenpos;
        Buffer[] b = ObjectCreator.createQuadPrismBuffers(new Vector3f(0, 0, 0), new Vector3f(size.x, size.y, 0));
        this.d = new TexturedDrawnObject((FloatBuffer) b[0], (IntBuffer) b[1], g);
    }

}
