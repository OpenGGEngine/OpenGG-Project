/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.particle.ExplosionParticleEmitter;

/**
 *
 * @author Javier
 */
@ForComponent(ExplosionParticleEmitter.class)
public class ExplosionParticleEmitterViewModel extends ViewModel<ExplosionParticleEmitter>{

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var tex = new DataBinding.TextureBinding();
        tex.name = "Particle texture";
        tex.internalname = "tex";
        tex.setValueAccessorFromData(() -> component.getTexture().getData().get(0));
        tex.onViewChange(component::setTexture);
        
        var life = new DataBinding.FloatBinding();
        life.name = "Particle life";
        life.internalname = "life";
        life.setValueAccessorFromData(component::getLifeLength);
        life.onViewChange(component::setLifeLength);

        var amount = new DataBinding.IntBinding();
        amount.name = "Amount per explosion";
        amount.internalname = "amount";
        amount.setValueAccessorFromData(component::getAmount);
        amount.onViewChange(component::setAmount);

        var vel = new DataBinding.FloatBinding();
        vel.name = "Particle velocity";
        vel.internalname = "vel";
        vel.setValueAccessorFromData(component::getVelocity);
        vel.onViewChange(component::setVelocity);


        this.addElement(tex);
        this.addElement(life);
        this.addElement(vel);
        this.addElement(amount);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public ExplosionParticleEmitter getFromInitializer(Initializer init) {
        return new ExplosionParticleEmitter(1,1, Texture.get2DTexture(TextureManager.getDefault()));
    }

}
