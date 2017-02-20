/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.world.Action;
import com.opengg.core.world.Actionable;

/**
 *
 * @author Javier
 */
public class UserControlComponent extends Component{
    public int userid;
    
    public void doAction(Action action){
        ((Actionable)parent).onAction(action);
    }
    
    @Override
    public void setParentInfo(Component parent){
        super.setParentInfo(parent);
        if(!(parent instanceof Actionable)){
            throw new InvalidParentException("Controllers must have actionables as parents!");
        }
    }
}
