/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui.text;

import com.opengg.core.gui.GUIRenderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;

/**
 *
 * @author Javier
 */
public class GUITextLine extends GUIRenderable {
    public Text text;
    Font font;

    public GUITextLine(Text text, Font font) {
        this.text = text;
        this.font = font;

        this.setRenderable(font.createFromText(text));
    }

    public GUITextLine(Font font) {
        this(Text.from(""), font);
    }

    public void setText(String ntext) {
        this.text.setText(ntext);
        this.setRenderable(font.createFromText(text));
    }

    public void setText(Text text) {
        this.text = text;
        this.setRenderable(font.createFromText(text));
    }

    public String getContents(){
        return text.getText();
    }

    public void setFont(Font font) {
        this.font = font;
        this.setRenderable(font.createFromText(text));
    }

    @Override
    public void render() {
        super.render();
        ShaderController.useConfiguration("gui");
    }
}
