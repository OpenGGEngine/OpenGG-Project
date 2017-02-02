/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.light;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class Light {
    public Vector3f pos = new Vector3f();
    public Vector3f color = new Vector3f();
    public float distance = 10;
    public float strength = 10;
    public boolean shadows = false;
}
