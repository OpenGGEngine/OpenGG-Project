/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.TriggerableAudioComponent;
import com.opengg.core.world.components.particle.DirectionalExplosionParticleEmitter;
import com.opengg.core.world.components.particle.ExplosionParticleEmitter;
import com.opengg.core.world.components.triggers.Trigger;

/**
 *
 * @author Javier
 */
public class GunComponent extends Component{
    Sound gunfire;
    Trigger trigger;
    
    public GunComponent(){
        super();
        trigger = new Trigger();
        ExplosionParticleEmitter epm = new DirectionalExplosionParticleEmitter(5, 0.1f, new Vector3f(1,0,0), 45, Resource.getTexture("smoke.png"));
        epm.setPositionOffset(new Vector3f(1,0.5f,0));
        epm.setParticleAmountOnTrigger(30);
        ModelRenderComponent beretta = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\smithwesson\\smithwesson.bmf"));
        TriggerableAudioComponent tac = new TriggerableAudioComponent(new Sound(Resource.getSoundData("45acp.ogg")));
        trigger.addSubscriber(tac);
        trigger.addSubscriber(epm);
        attach(epm);
        attach(tac);
        attach(trigger);
        attach(beretta);
        gunfire = new Sound("C:\\res\\othergun.ogg");
    }
    
    public void fire(){
        BulletComponent bullet = new BulletComponent(this);
        WorldEngine.getCurrent().attach(bullet);
        trigger.trigger(null);
    }
}
