/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class WorldObject extends Component{
    public WorldObject() {
        super();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
    }
}
