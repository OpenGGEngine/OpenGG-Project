/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.BindController;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.Actionable;

/**
 *
 * @author Javier
 */
public class ActionTransmitterComponent extends ControlledComponent implements ActionTransmitter{

    @Override
    public void onEnable(){
        if(this.getWorld().isPrimaryWorld() || this.isEnabledAcrossWorlds()) BindController.addController(this);
    }

    @Override
    public void onDisable(){
        BindController.removeController(this);
    }

    @Override
    public void onWorldMadePrimary(){
        if(!this.isEnabledAcrossWorlds())
            if(isEnabled())
                BindController.addController(this);
    }

    @Override
    public void onWorldNoLongerPrimary(){
        if(!this.isEnabledAcrossWorlds())
            BindController.removeController(this);
    }

    @Override
    public void doAction(Action action){
        if(isCurrentUser()) ((Actionable)getParent()).onAction(action);
    }

    @Override
    public void onParentChange(Component parent){
        if(!(parent instanceof Actionable)){
            throw new InvalidParentException("Controllers must have actionables as parents!");
        }
    }
}
