package com.opengg.core.vr;

import com.opengg.core.world.Camera;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.Component;

public class VRHMDComponent extends Component {
    private CameraComponent camera;

    public VRHMDComponent(){
        this.attach(camera = new CameraComponent());
    }

    @Override
    public void update(float delta){

        var values = GGVR.getHMDPose();

        camera.use();
    }
}
