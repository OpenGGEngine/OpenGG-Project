/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.colliders.BoundingBox;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.triggers.TriggerInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author ethachu19
 */
public class Zone extends TriggerComponent {
    private BoundingBox box;
    private final boolean repeat = false;
    private final List<Component> lastFrames = new ArrayList<>();

    public Zone(){
        box = new BoundingBox(0,0,0);
    }
    
    public Zone(BoundingBox box){
        this.box = box;
    }

    public Zone(BoundingBox box, Consumer<TriggerInfo> triggerInfoConsumer){
        this.box = box;
        this.addListener((s,i) -> triggerInfoConsumer.accept(i));
    }

    public void setBox(BoundingBox box) {
        this.box = box;
    }

    @Override
    public void update(float delta){
        checkForCollisions();
    }
    
    public void checkForCollisions(){
        box.setPosition(this.getPosition());
        box.recalculate();
        for(var collidingComp : this.getWorld().getAllDescendants()){
            if(collidingComp == this) continue;
            if(lastFrames.contains(collidingComp)) continue;
            if(box.isColliding(collidingComp.getPosition())){
                lastFrames.add(collidingComp);
                var ti = new TriggerInfo(collidingComp, "collide:" + collidingComp.getGUID(), TriggerInfo.TriggerType.SINGLE);
                trigger(ti);
            }
        }

        var extra = new ArrayList<Component>();
        for(var c : lastFrames){
            if(!box.isColliding(c.getPosition())) extra.add(c);
        }

        lastFrames.removeAll(extra);
    }

    public BoundingBox getBox() {
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
        box = new BoundingBox(lwh);
        box.setPosition(pos);
    }
}
