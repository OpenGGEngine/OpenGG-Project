package com.opengg.core.world.components;

import com.opengg.core.Configuration;
import com.opengg.core.GGInfo;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.server.ServerClient;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

/**
 * Represents a component that is specific to a single human player <br>
 *
 */
public class ControlledComponent extends Component{
    private int userid = 0;
    private boolean enableAcrossWorlds = false;

    /**
     * Returns the mouse position of the player assigned to this component
     * @return
     */
    public Vector2f getMouse(){
        if(!GGInfo.isServer() && isCurrentUser()){
            return MouseController.get().multiply(Configuration.getFloat("sensitivity"));
        }else{
            if(GGInfo.isServer()){
                return NetworkEngine.getServer().getClientByID(userid).map(ServerClient::getMousePosition).orElse(new Vector2f());
            }
        }

        return new Vector2f();
    }

    /**
     * Returns if the user assigned to this component is the same user as the one running this instance of the engine
     * @return
     */
    public boolean isCurrentUser(){
        return (userid == GGInfo.getUserId() || GGInfo.getUserId() == -1) &&
                ((this.getWorld() != null && this.getWorld().isPrimaryWorld()) || enableAcrossWorlds) ;
    }

    /**
     * Returns the User ID assigned to this component
     * @return
     */
    public int getUserId() {
        return userid;
    }

    public Component setUserId(int userid) {
        this.userid = userid;
        onUserChange();
        return this;
    }

    /**
     * Returns if this component is active if its world is active but not the primary world
     * @return
     */
    public boolean isEnabledAcrossWorlds() {
        return enableAcrossWorlds;
    }

    /**
     * Sets if this component should be considered active if its world is active but is not the primary world
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
        var userId = in.readInt();
        setUserId(userId);
    }
}
