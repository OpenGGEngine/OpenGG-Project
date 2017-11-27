/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class Force {
    public Vector3f force = new Vector3f();
    public float velLimit = 10;
    public boolean frictionDisable = false;
}
