package com.opengg.test.network.components;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;

import java.io.IOException;

public abstract class Projectile extends Component {
    float damage;
    RenderComponent render;

    public Projectile(float damage) {
        this.damage = damage;
    }

    public void setView(RenderComponent view){
        this.render = view;
        view.setSerializable(false);
        this.attach(view);
    }

    public abstract RenderComponent generateView();

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(damage);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        damage = in.readFloat();
        setView(generateView());
    }
}
