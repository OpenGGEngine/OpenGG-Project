package com.opengg.core.vr;

import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.Component;

public class VRHMDComponent extends Component {
    private final CameraComponent camera;

    public VRHMDComponent(){
        this.attach(camera = new CameraComponent());
    }

    @Override
    public void update(float delta){

        var values = VR.getHMDPose();
    }
}
