/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.entities.resources;

import com.opengg.core.math.Vector3f;
import java.io.Serializable;

/**
 *
 * @author Javier
 */
public class EntityFrame implements Serializable{
    public short id;
    private float height = 5f, width = 5f, length = 5f;
    public short currentWorld = 0;
    public Vector3f pos = new Vector3f();
    public Vector3f force = new Vector3f();
    public Vector3f airResistance = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
}
