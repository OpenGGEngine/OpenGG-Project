/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import com.opengg.core.render.particle.ParticleType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class ParticleRenderComponent implements Component {
    List<ParticleType> particles = new ArrayList<>();
    @Override
    public void update() {
        particles.stream().forEach((p) -> {
            p.update();
        });
    }


    public void render() {
        
    }
    
    
}