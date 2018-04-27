/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.test;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

/**
 *
 * @author Javier
 */
public class EnemySpawnerComponent extends Component implements Triggerable{
    public EnemyComponent current;
    public boolean kill = false;
    
    public EnemySpawnerComponent(){
        current = new EnemyComponent();
        WorldEngine.getCurrent().attach(current);
    }
    
    public void update(float delta){
        if(kill == true){
            kill = false;
            current = new EnemyComponent();
        }
    }
    
    @Override
    public void onTrigger(Trigger source, TriggerInfo info) {
        kill = true;
    }

    @Override
    public void onSubscribe(Trigger trigger) {
    }
    
}
