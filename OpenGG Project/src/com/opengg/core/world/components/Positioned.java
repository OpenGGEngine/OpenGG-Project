/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public interface Positioned extends Component {
    public void setPosition(Vector3f pos);
    public void setRotation(Vector3f rot);
    public Vector3f getPosition();
    public Vector3f getRotation();
}
