package com.opengg.core.world.components;

import com.opengg.core.GGInfo;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class ControlledComponent extends Component{
    private int userid = 0;

    public final void useComponent(){
        if(isCurrentUser()) use();
    }

    public void use(){

    }

    public boolean isCurrentUser(){
        return userid == GGInfo.getUserId();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(userid);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        userid = in.readInt();
    }
}
