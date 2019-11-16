package com.opengg.core.physics.mechanics;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsProvider;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class SpringGenerator extends ForceGenerator{
    private RigidBody base;
    private RigidBody object;
    private Vector3f point;
    private float springConstant;

    public SpringGenerator(){}

    public SpringGenerator(RigidBody base, RigidBody object, float springConstant) {
        this.base = base;
        this.object = object;
        this.springConstant = springConstant;
    }

    public SpringGenerator(RigidBody object, Vector3f point, float springConstant) {
        this.object = object;
        this.point = point;
        this.springConstant = springConstant;
    }

    @Override
    public void applyTo(PhysicsProvider entity) {
        if(entity.parent == object){
            if(base == null){
                entity.addForceForTick(
                        object.getPosition().subtract(point).inverse()
                        .multiply(springConstant)
                );
            }else {
                entity.addForceForTick(object.getPosition().subtract(base.getPosition()).inverse()
                        .multiply(springConstant)
                );
            }
        }else if(entity.parent == base){
            entity.addForceForTick(base.getPosition().subtract(object.getPosition()).inverse()
                    .multiply(springConstant)
            );
        }
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        out.write(this.getPosition());
        out.write(object.getId());
        out.write(base != null);
        if(base != null)
            out.write(base.getId());
        else
            out.write(point);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        this.setPosition(in.readVector3f());
        long oid = in.readLong();
        OpenGG.asyncExec(() -> this.object = (RigidBody) this.system.getObjectByID(oid));
        if(in.readBoolean()){
            long bid = in.readLong();
            OpenGG.asyncExec(() -> this.base = (RigidBody) this.system.getObjectByID(bid));
        }else{
            point = in.readVector3f();
        }
    }
}
