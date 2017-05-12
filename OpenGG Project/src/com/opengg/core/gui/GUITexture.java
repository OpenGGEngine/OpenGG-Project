/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Warren
 */
public class GUITexture extends VisualGUIItem {
    public GUITexture(Texture tex, Vector2f screenpos, Vector2f size) {
        Buffer[] b = ObjectCreator.createQuadPrismBuffers(new Vector3f(0, 0, 0), new Vector3f(size.x, size.y, 0));
        this.setDrawable(new TexturedDrawnObject((FloatBuffer) b[0], (IntBuffer) b[1], tex));
        this.setPos(pos);
    }
}
