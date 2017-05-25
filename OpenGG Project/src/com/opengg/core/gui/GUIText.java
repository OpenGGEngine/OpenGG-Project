/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.Text;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.text.GGFont;

/**
 *
 * @author Javier
 */
public class GUIText extends GUIRenderable{
    Text text;
    GGFont font;
    public GUIText(Text text, GGFont font, Vector2f pos){
        this.text = text;
        this.font = font;
        this.setPositionOffset(pos);
        this.setDrawable(text.getDrawable(font));
    }
    
    public GUIText(GGFont font, Vector2f pos){
        this.text = new Text();
        this.font = font;
        this.setPositionOffset(pos);
        this.setDrawable(text.getDrawable(font));
    }
    
    public void setText(String ntext){
        this.text.setText(ntext);
        this.setDrawable(text.getDrawable(font));
    }
    
    public void setText(Text text){
        this.text = text;
        this.setDrawable(text.getDrawable(font));
    }
    
    public void setFont(GGFont font){
        this.font = font;
        this.setDrawable(text.getDrawable(font));
    }
    
    @Override
    public void render(){
        ShaderController.setDistanceField(1);
        super.render();
        ShaderController.setDistanceField(0);
    }
}
