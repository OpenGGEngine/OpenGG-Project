/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public interface Positioned extends Component {
    public void setPosition(Vector3f pos);
    public default void setRotation(Quaternionf rot){}
    public default void setScale(Vector3f v){}
    
    public Vector3f getPosition();
    public default Quaternionf getRotation(){
        return new Quaternionf();
    }
    public default Vector3f getScale(){
        return new Vector3f();
    }
        
}
