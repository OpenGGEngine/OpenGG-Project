/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.particle.ExplosionParticleEmitter;
import com.opengg.core.world.components.particle.ParticleEmitter;

/**
 *
 * @author Javier
 */
public class ExplosionParticleEmitterViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element tex = new Element();
        tex.type = Element.TEXTURE;
        tex.name = "Particle texture";
        tex.value = TextureManager.getDefault();
        tex.internalname = "tex";
        
        Element life = new Element();
        life.type = Element.FLOAT;
        life.name = "Particle life";
        life.value = 1f;
        life.internalname = "life";
        
        Element amount = new Element();
        amount.type = Element.INTEGER;
        amount.name = "Amount per explosion";
        amount.value = 1;
        amount.internalname = "amount";
        
        Element vel = new Element();
        vel.type = Element.FLOAT;
        vel.name = "Particle velocity";
        vel.value = 1f;
        vel.internalname = "vel";
        
        elements.add(tex);
        elements.add(life);
        elements.add(vel);
        elements.add(amount);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        return new ExplosionParticleEmitter(1,1,Texture.get2DTexture(TextureManager.getDefault()));
    }

    @Override
    public void onChange(Element element) {
        switch(element.internalname){
            case "tex":
                ((ParticleEmitter)component).setTexture((Texture) element.value);
                break;
            case "life":
                ((ParticleEmitter)component).setLifeLength((Float) element.value);
                break;
            case "amount":
                ((ExplosionParticleEmitter)component).setAmount((Integer) element.value);
                break;
            case "vel":
                ((ExplosionParticleEmitter)component).setVelocity((Float) element.value);
                break;
        }
    }

    @Override
    public void updateViews() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
