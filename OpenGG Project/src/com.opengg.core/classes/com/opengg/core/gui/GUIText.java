/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;

import java.awt.*;

/**
 *
 * @author Javier
 */
public class GUIText extends GUIRenderable {

    public Text text;
    Font font;

    public GUIText(Text text, Font font, Vector2f pos) {
        this.text = text;
        this.font = font;
        this.setPositionOffset(pos);
        this.setDrawable(font.createFromText(text));
    }

    public GUIText(Text text, Font font) {
        this(text, font, new Vector2f());
    }

    public GUIText(Font font, Vector2f pos) {
        this(Text.from(""), font, pos);
    }

    public void setText(String ntext) {
        this.text.setText(ntext);
        this.setDrawable(font.createFromText(text));
    }

    public void setText(Text text) {
        this.text = text;
        this.setDrawable(font.createFromText(text));
    }

    public void setFont(Font font) {
        this.font = font;
        this.setDrawable(font.createFromText(text));
    }

    @Override
    public void render() {
        ShaderController.useConfiguration("text");
        super.render();
    }
}
