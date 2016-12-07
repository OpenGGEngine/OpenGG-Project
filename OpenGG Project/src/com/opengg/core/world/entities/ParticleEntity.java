package com.opengg.core.world.entities;


import com.opengg.core.math.Vector3f;


public class ParticleEntity {

	public Vector3f position;
        public Vector3f velocity;
        public float timetodespawn;
	public boolean destroyed = false;
	
	
	public ParticleEntity(Vector3f pos){
		this.position = new Vector3f(pos);
	}
}