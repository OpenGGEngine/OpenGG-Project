/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

/**
 *
 * @author ethachu19
 */
public class EntityEnums {
    public enum EntityType {
        /* Update Movement, Force Update, No Collsion Response*/ Static,
        /* Update Movement, Force Update, Collision Detection*/ Physics,
        /* Update Movement, No force update, No Collision*/ Particle,
        /* User Defined*/ Other
    }

    public enum Collide {
        Collidable, Uncollidable, NoResponse
    }

    public enum UpdateXYZ {
        Movable, Immovable
    }

    public enum UpdateForce {
        Realistic, Unrealistic
    }
}
