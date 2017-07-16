/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.Actionable;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class UserControlComponent extends Component implements ActionTransmitter{
    public int userid;
    
    @Override
    public void doAction(Action action){
        ((Actionable)getParent()).onAction(action);
    }
    
    @Override
    public void setParentInfo(Component parent){
        super.setParentInfo(parent);
        if(!(parent instanceof Actionable)){
            throw new InvalidParentException("Controllers must have actionables as parents!");
        }
    }
    
    @Override
    public void serialize(GGByteOutputStream out) throws IOException{
        super.serialize(out);
        out.write(userid);
    }
    
    @Override
    public void deserialize(GGByteInputStream in) throws IOException{
        super.deserialize(in);
        userid = in.readInt();
    }
}
