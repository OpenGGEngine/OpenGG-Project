/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package particle;

import com.opengg.core.Vector3f;

/**
 *
 * @author Warren
 */
public class Particle {
    private Vector3f position;
    private Vector3f velocity;
    private Vector3f gravity;
    private float timeAlive = 0;
    private float timeOfLife;
    private float scale;
    
    public Particle(Vector3f position, Vector3f velocity, Vector3f gravity, float lifeLength, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravity = gravity;
        this.timeOfLife = lifeLength;
        this.scale = scale;
    }
    
    public boolean update(){
        position.add(velocity,gravity);
        timeAlive++;
        return timeAlive > timeOfLife;
    }
    
    
    
}
