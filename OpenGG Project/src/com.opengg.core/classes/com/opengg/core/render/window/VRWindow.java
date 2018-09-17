package com.opengg.core.render.window;

import com.opengg.core.exceptions.WindowCreationException;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;

import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.*;

public class VRWindow implements Window{


    @Override
    public void setup(WindowInfo info) {
        IntBuffer errHandle = IntBuffer.allocate(1);
        int vrHandle = VR.VR_InitInternal(errHandle, VR.EVRApplicationType_VRApplication_Scene);

        if(!VR_IsRuntimeInstalled())
            throw new WindowCreationException("Failed to find OpenVR runtime");
        
        if(!VR_IsHmdPresent())
            throw new WindowCreationException("Failed to find HMD");


        int err = errHandle.get(0);
        if(err != 0){
            throw new WindowCreationException("Failed to initialize VR context");
        }

        OpenVR.create(vrHandle);

        //if(!VRCompositor())

        strDriver = getTrackedDeviceString(vrHandle, k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_TrackingSystemName_String);
    }

    @Override
    public void endFrame() {

    }

    @Override
    public float getRatio() {
        return 0;
    }

    @Override
    public boolean shouldClose() {
        return false;
    }

    @Override
    public void destroy() {
        VR_ShutdownInternal();
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean getSuccessfulConstruction() {
        return false;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setIcon(String path) throws Exception {

    }

    @Override
    public void setVSync(boolean vsync) {

    }

    @Override
    public void setCurrentContext() {

    }
}
