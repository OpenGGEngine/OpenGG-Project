package com.opengg.test.network.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.collision.colliders.ConvexHull;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Zone;
import com.opengg.core.world.components.physics.RigidBodyComponent;

import java.io.IOException;
import java.util.List;

public class WorldItemContainer extends Component {
    public WorldItemContainer(){
        var collider = List.of(
                new Vector3f(-0.07f,-0.03f,-0.07f),
                new Vector3f(-0.07f,-0.03f,0.07f),
                new Vector3f(0.07f,-0.03f,-0.07f),
                new Vector3f(0.07f,-0.03f,0.07f),
                new Vector3f(-0.07f,0.3f,-0.07f),
                new Vector3f(-0.07f,0.3f,0.07f),
                new Vector3f(0.07f,0.3f,-0.07f),
                new Vector3f(0.07f,0.3f,0.07f)
        );
        this.attach(new RigidBodyComponent(new RigidBody(new AABB(0.5f,0.5f,0.5f), new ConvexHull(collider)), true));
    }


    public WorldItemContainer(Item item){
        this();
        setItem(item);
    }

    public void setItem(Item item){

        item.setName("item");
        this.attach(item);
        this.attach(new Zone(new AABB(1,1,1), i -> {
            if(i.data instanceof Character) {
                OpenGG.asyncExec(() -> {
                    ((Character) i.data).useItem(item);
                    this.delete();
                });
            }
        }).setSerializable(false));
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        OpenGG.asyncExec(() -> setItem((Item) this.findByName("item").get(0)));
    }
}
