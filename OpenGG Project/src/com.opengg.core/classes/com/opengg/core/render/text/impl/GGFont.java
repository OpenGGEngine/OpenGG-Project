/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.text.impl;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import java.io.File;

/**
 *
 * @author Warren
 */
public class GGFont implements Font {
    //ok basically this looks like a mess and it is.
    //The textvbogenerator is what the name says it is. Every font has its own generator with specific parameters
    public Texture texture;
    public TextVBOGenerator badname;
    
    public GGFont(String texture, String fontFile) {
	this.texture = Texture.get2DTexture(texture);
	this.badname = new TextVBOGenerator(new File(fontFile));
    }
    
    public GGFont(Texture texture, String fontFile) {
	this.texture = texture;
	this.badname = new TextVBOGenerator(new File(fontFile));
    }

    @Override
    public Drawable createFromText(Text text) {
        return badname.createTextData(text, this);
    }

    @Override
    public Drawable createFromText(String text) {
        return this.createFromText(Text.from(text));
    }

}