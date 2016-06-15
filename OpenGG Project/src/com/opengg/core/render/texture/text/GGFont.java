/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture.text;

import com.opengg.core.gui.GUIText;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.texture.Texture;
import java.io.File;

/**
 *
 * @author Warren
 */
public class GGFont {
    //ok basically this looks like a mess and it is.
    //The textvbogenerator is what the name says it is. Every font has its own generator with specific parameters
    public Texture texture;
    public TextVBOGenerator badname;
    public GGFont(Texture texture, File fontFile) {
	this.texture = texture;
	this.badname = new TextVBOGenerator(fontFile);
    }
    public TexturedDrawnObject loadText(GUIText text) {
        return badname.createTextData(text);
    }
}
