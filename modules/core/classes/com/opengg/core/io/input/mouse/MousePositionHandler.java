/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.mouse;

import com.opengg.core.math.Vector2f;

/**
 *
 * @author Javier
 */
public interface MousePositionHandler{
    double getX();
    double getY();
    Vector2f getPos();
    Vector2f getRawPos();
}
