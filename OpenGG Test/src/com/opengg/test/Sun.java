/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */

 
public class Sun {
 
    private static final float SUN_DIS = 800;// fairly arbitrary - but make sure
                                            // it doesn't go behind skybox
 
    private final Texture texture;
 
    private Vector3f lightDirection = new Vector3f(0, -1, 0);
    private float scale;
 
    public Sun(Texture texture, float scale) {
        this.texture = texture;
        this.scale = scale;
    }
 
    public void setScale(float scale) {
        this.scale = scale;
    }
 
    public void setDirection(float x, float y, float z) {
        lightDirection.set(x, y, z);
        lightDirection.normalize();
    }
 
    public Texture getTexture() {
        return texture;
    }
 
    public Vector3f getLightDirection() {
        return lightDirection;
    }
 
    public float getScale() {
        return scale;
    }

    public Vector3f getWorldPosition(Vector3f camPos) {
        Vector3f sunPos = new Vector3f(lightDirection);
        sunPos.negate();
        sunPos.multiplyThis(SUN_DIS);
        return new Vector3f().add(sunPos).add(camPos);
        
    }
 
}
