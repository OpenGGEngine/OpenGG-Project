/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.ShaderProgram;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.world.Camera;

/**
 *
 * @author Javier
 */
public class GUI {
    Camera c;
    ShaderProgram sp;
    Vector2f low,high;
    int lightpos;
    public void setupGUI(ShaderProgram sp, Vector2f lowBound, Vector2f highBound){
        this.sp = sp;
        low = lowBound;
        high = highBound;
        lightpos = this.sp.getUniformLocation("lightpos");
        c = new Camera(sp,new Vector3f(0,0,0), new Vector3f(0,0,0));
    }
    public void startGUI(){
        ViewUtil.setOrtho(low.x, high.x, low.y, high.y, 0.2f, 10, sp);      
        sp.setUniform(lightpos, new Vector3f(0,50,0));
        c.setPos(new Vector3f(0,0,0));
        c.setRot(new Vector3f(0,0,0));
        c.use();
    }
}
