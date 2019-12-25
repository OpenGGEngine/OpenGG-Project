package com.opengg.test.network.components;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;

import java.io.IOException;

public abstract class Item extends Component {
    private String name;
    private RenderComponent render;

    public Item(String name) {
        this.name = name;
        this.setName("item");
    }

    public Item(String name, RenderComponent view) {
        this(name);
        setView(view);
    }

    @Override
    public void onEnable(){
        render.setEnabled(true);
    }

    @Override
    public void onDisable(){
        render.setEnabled(false);
    }

    public void setView(RenderComponent view){
        this.render = view;
        view.setSerializable(false);
        this.attach(view);
    }

    public abstract RenderComponent generateView();

    public abstract boolean use();

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(name);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        name = in.readString();
        setView(generateView());
    }
}
