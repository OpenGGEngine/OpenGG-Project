/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.physics.resources;

import com.opengg.core.Vector3f;
import java.io.Serializable;

/**
 *
 * @author Javier
 */
public class PhysicsStruct implements Serializable{
    public static Vector3f gravityVector = new Vector3f( 0,1,0);
    public static Vector3f wind = new Vector3f();
}
