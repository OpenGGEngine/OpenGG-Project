package com.opengg.core.vr;

import com.opengg.core.world.components.Component;

public class VRControllerComponent extends Component {
    private final VR.Controller side;

    private int id;

    public VRControllerComponent(VR.Controller controller){
        this.side  = controller;
    }

    @Override
    public void update(float delta){
        //this.setPositionOffset()
    }
}
