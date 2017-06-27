/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelRenderComponent;

/**
 *
 * @author Javier
 */
public class GunComponent extends Component{
    //Sound gunfire;
    
    public GunComponent(){
        super();
        ModelRenderComponent beretta = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\smithwesson\\smithwesson.bmf"));
        attach(beretta);
        //gunfire = new Sound("C:\\res\\othergun.ogg");
    }
    
    public void fire(){
        BulletComponent b = new BulletComponent(this);
        WorldEngine.getCurrent().attach(b);
        //gunfire.stop();
        //gunfire.play();
    }
}
