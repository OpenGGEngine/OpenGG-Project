/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.text.impl;

import com.opengg.core.render.Renderable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;

import java.io.File;

import static org.lwjgl.opengl.GL11.GL_LINEAR;

/**
 * @author Warren
 */
public class GGFont implements Font {
    //ok basically this looks like a mess and it is.
    //The textvbogenerator is what the name says it is. Every font has its own generator with specific parameters
    public Texture texture;
    public TextVBOGenerator badname;

    public GGFont(String texture, String fontFile) {
        //Use of Texture.create is necessary because setMinFilter does not appear to work.
        //Both min and max filters must be on GL_LINEAR for distance fields to work.
        this.texture = Texture.create(Texture.config().minimumFilter(Texture.FilterType.LINEAR).maxFilter(Texture.FilterType.LINEAR),texture);
        this.badname = new TextVBOGenerator(new File(fontFile));
    }

    public GGFont(Texture texture, String fontFile) {
        this.texture = texture;
        this.badname = new TextVBOGenerator(new File(fontFile));
    }

    @Override
    public Renderable createFromText(Text text) {
        return badname.createTextData(text, this);
    }

    @Override
    public Renderable createFromText(String text) {
        return this.createFromText(Text.from(text));
    }

}
