/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import java.io.IOException;

/**
 *
 * @author ethachu19
 */
public class Zone extends Trigger{
    AABB box;
    
    public Zone(){
        box = new AABB(0,0,0);
    }
    
    public Zone(AABB box){
        this.box = box;
    }
    
    public void checkForCollisions(){
        for(Component c : this.getWorld().getAll()){
            if(box.isColliding(c.getPosition())){
                TriggerInfo ti = new TriggerInfo();
                ti.source = this;
                ti.data = c;
                ti.type = 0;
                ti.info = "collide:" + c.getId();
                trigger(ti);
            }
        }
    }
    
    @Override
    public void onPositionChange(Vector3f npos){
        box.setPosition(npos);
        box.recalculate();
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(box.getLWH());
        out.write(box.getPosition());
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        Vector3f lwh = in.readVector3f();
        Vector3f pos = in.readVector3f();
        box = new AABB(lwh.x(),lwh.y(),lwh.z());
        box.setPosition(pos);
    }
}
