/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui.text;

import com.opengg.core.gui.UIRenderable;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;

/**
 *
 * @author Javier
 */
public class UITextLine extends UIRenderable {
    protected Text text;
    protected Font font;

    private Vector3fm size;

    public UITextLine(Text text, Font font) {
        this.text = text;
        this.font = font;
        this.size = new Vector3fm();

        this.setRenderable(font.createFromTextWithSize(text, size));
    }

    public UITextLine(Font font) {
        this(Text.from(""), font);
    }

    public void setText(String ntext) {
        this.text.setText(ntext);
        this.setRenderable(font.createFromTextWithSize(text, size));
    }

    public void setText(Text text) {
        this.text = text;
        this.setRenderable(font.createFromTextWithSize(text, size));
    }

    public String getContents(){
        return text.getText();
    }

    public void setFont(Font font) {
        this.font = font;
        this.setRenderable(font.createFromText(text));
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(size.x, -size.y);
    }

    @Override
    public void render() {
        super.render();
        ShaderController.useConfiguration("gui");
    }
}
