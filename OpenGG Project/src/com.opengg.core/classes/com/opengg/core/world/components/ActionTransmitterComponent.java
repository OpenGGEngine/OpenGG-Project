/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.BindController;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.Actionable;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class ActionTransmitterComponent extends ControlledComponent implements ActionTransmitter{

    @Override
    public void use(){
        BindController.addController(this);
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

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        if(isCurrentUser()) use();
    }
}
