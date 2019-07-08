package com.opengg.core.world.components;

import com.opengg.core.Configuration;
import com.opengg.core.GGInfo;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class ControlledComponent extends Component{
    private int userid = 0;
    private boolean enableAcrossWorlds = false;

    public Vector2f getMouse(){
        if(!GGInfo.isServer() && isCurrentUser()){
            return MouseController.get().multiply(Configuration.getFloat("sensitivity"));
        }else{
            if(GGInfo.isServer()){
                var user = NetworkEngine.getServer().getByID(userid);
                if(user != null) return user.getMousePosition();
            }
        }

        return new Vector2f();
    }

    public boolean isCurrentUser(){
        return (userid == GGInfo.getUserId() || GGInfo.getUserId() == -1) && (this.getWorld().isPrimaryWorld() || enableAcrossWorlds) ;
    }

    public int getUserId() {
        return userid;
    }

    public Component setUserId(int userid) {
        this.userid = userid;
        onUserChange();
        return this;
    }

    /**
     * Returns if this component is active if it's world is active but not the primary world
     * @return
     */
    public boolean isEnabledAcrossWorlds() {
        return enableAcrossWorlds;
    }

    /**
     * Sets if this component should be considered active if its world is active but not the primary world
     * @param enableAcrossWorlds
     */
    public void setEnabledAcrossWorlds(boolean enableAcrossWorlds) {
        this.enableAcrossWorlds = enableAcrossWorlds;
    }

    public void onUserChange(){

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
