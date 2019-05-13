/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Contact {
    Vector3f position;
    Vector3f normal;
    float depth;

    public Contact() {
    }

    public Contact(Vector3f position, Vector3f normal, float depth) {
        this.position = position;
        this.normal = normal;
        this.depth = depth;
    }
}








