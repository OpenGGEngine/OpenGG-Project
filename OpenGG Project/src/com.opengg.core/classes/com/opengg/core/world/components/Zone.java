/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class Zone extends Trigger{
    private AABB box;
    private boolean repeat = false;
    List<Component> lastFrames = new ArrayList<>();

    public Zone(){
        box = new AABB(0,0,0);
    }
    
    public Zone(AABB box){
        this.box = box;
    }

    public void setBox(AABB box) {
        this.box = box;
    }

    @Override
    public void update(float delta){
        checkForCollisions();
    }
    
    public void checkForCollisions(){
        for(Component c : this.getWorld().getAll()){
            if(c == this) continue;
            if(lastFrames.contains(c)) continue;
            if(box.isColliding(c.getPosition())){
                lastFrames.add(c);
                TriggerInfo ti = new TriggerInfo();
                ti.source = this;
                ti.data = c;
                ti.type = 0;
                ti.info = "collide:" + c.getId();
                trigger(ti);
                onTrigger(ti);
            }
        }

        var extra = new ArrayList<Component>();
        for(var c : lastFrames){
            if(!box.isColliding(c.getPosition())) extra.add(c);
        }

        lastFrames.removeAll(extra);
    }

    public AABB getBox() {
        return box;
    }   
    
    @Override
    public void onPositionChange(Vector3f npos){
        box.setPosition(npos);
        box.recalculate();
    }
    
    @Override
    public void onRotationChange(Quaternionf nrot){
        box.setRotation(nrot);
        box.recalculate();
    }
    
    @Override
    public void onScaleChange(Vector3f nscale){
        box.setScale(nscale);
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
        box = new AABB(lwh);
        box.setPosition(pos);
    }
}
