/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.particle.ExplosionParticleEmitter;

/**
 *
 * @author Javier
 */
@ForComponent(ExplosionParticleEmitter.class)
public class ExplosionParticleEmitterViewModel extends ComponentViewModel<ExplosionParticleEmitter> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var tex = new DataBinding.TextureBinding();
        tex.name = "Particle texture";
        tex.internalname = "tex";
        tex.setValueAccessorFromData(() -> model.getTexture().getData().get(0));
        tex.onViewChange(model::setTexture);
        
        var life = new DataBinding.FloatBinding();
        life.name = "Particle life";
        life.internalname = "life";
        life.setValueAccessorFromData(model::getLifeLength);
        life.onViewChange(model::setLifeLength);

        var amount = new DataBinding.IntBinding();
        amount.name = "Amount per explosion";
        amount.internalname = "amount";
        amount.setValueAccessorFromData(model::getAmount);
        amount.onViewChange(model::setAmount);

        var vel = new DataBinding.FloatBinding();
        vel.name = "Particle velocity";
        vel.internalname = "vel";
        vel.setValueAccessorFromData(model::getVelocity);
        vel.onViewChange(model::setVelocity);


        this.addElement(tex);
        this.addElement(life);
        this.addElement(vel);
        this.addElement(amount);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public ExplosionParticleEmitter getFromInitializer(BindingAggregate init) {
        return new ExplosionParticleEmitter(1,1, Texture.get2DTexture(TextureManager.getDefault()));
    }

}
