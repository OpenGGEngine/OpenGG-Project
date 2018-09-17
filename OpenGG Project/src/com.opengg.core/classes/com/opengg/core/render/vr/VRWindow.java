package com.opengg.core.render.vr;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.window.GLFWWindow;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;
import com.opengg.core.world.WorldEngine;
import org.lwjgl.openvr.*;

import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.*;

public class VRWindow implements Window {

    private Window window;
    private int recx, recy;
    private int devicecount;
    private int[] devices;

    @Override
    public void setup(WindowInfo info) {
        IntBuffer errHandle =  Allocator.allocInt(1);
        int vrHandle = VR.VR_InitInternal(errHandle, VR.EVRApplicationType_VRApplication_Scene);

        if(!VR_IsRuntimeInstalled())
            throw new WindowCreationException("Failed to find OpenVR runtime");
        
        if(!VR_IsHmdPresent())
            throw new WindowCreationException("Failed to find HMD");


        int err = errHandle.get(0);
        if(err != 0){
            throw new WindowCreationException("Failed to initialize VR context");
        }

        GGConsole.log("Initializing OpenVR system");

        OpenVR.create(vrHandle);
        //if(!VRCompositor())

        String driver = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_TrackingSystemName_String, errHandle);
        String model = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_ModelNumber_String, errHandle);
        String serial = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_SerialNumber_String, errHandle);

        GGConsole.log("Using " + driver + " tracking system, model " + model + " with serial number " + serial);

        int stationCount = 0;
        int wandCount = 0;

        devices = new int[k_unMaxTrackedDeviceCount-k_unTrackedDeviceIndex_Hmd];

        for(int i = k_unTrackedDeviceIndex_Hmd; i < k_unMaxTrackedDeviceCount; i++){
            if(VRSystem.VRSystem_IsTrackedDeviceConnected(i)){
                int deviceClass = VRSystem.VRSystem_GetTrackedDeviceClass(i);

                System.out.println(i + "   " + deviceClass);

                if(deviceClass == 4) stationCount++;
                if(deviceClass == 2) wandCount++;

                //GGConsole.log("Found device " + VRSystem.VRSystem_GetStringTrackedDeviceProperty(deviceClass, ETracked, errHandle));

                devices[i] = deviceClass;
            }
        }

        if(stationCount < 2) GGConsole.warning("Failed to find both base stations, is one disconnected?");

        IntBuffer w = Allocator.allocInt(1);
        IntBuffer h = Allocator.allocInt(1);

        VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);

        recx = w.get(0);
        recy = h.get(0);

        GGConsole.log("Rendering at " + recx + " x " + recy);

        info.width = recx;
        info.height = recy;

        window = new GLFWWindow(); window.setup(info);
    }

    @Override
    public void endFrame() {
        var event = (VREvent) null;
        while(VRSystem.VRSystem_PollNextEvent(event = VREvent.calloc())){
            //System.out.println(event.eventType());
        }

        var poseRenderBuffers = TrackedDevicePose.calloc(devicecount);
        var poseGameBuffers = TrackedDevicePose.calloc(devicecount);
        //VRCompositor.nVRCompositor_WaitGetPoses();
        poseRenderBuffers.free();
        poseGameBuffers.free();

        var hmdRenderBuffers = TrackedDevicePose.calloc(1);
        VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(ETrackingUniverseOrigin_TrackingUniverseStanding,0,hmdRenderBuffers);
        var m43 = hmdRenderBuffers.get(0).mDeviceToAbsoluteTracking();
        var matrix = VRUtil.fromVRMatrix43(m43);

        Camera camera = new Camera();
        RenderEngine.useCamera(camera);
        RenderEngine.getCurrentCamera().setPos(new Vector3f(matrix.m03, matrix.m13, matrix.m23));
        RenderEngine.getCurrentCamera().setRot(VRUtil.getQuaternionFrom43(m43));

        hmdRenderBuffers.free();

        var controllerstate = VRControllerState.calloc();
        var controllerpose = TrackedDevicePose.calloc();
        VRSystem.VRSystem_GetControllerStateWithPose(ETrackingUniverseOrigin_TrackingUniverseStanding,3, controllerstate, 1, controllerpose);
        m43 = hmdRenderBuffers.get(0).mDeviceToAbsoluteTracking();
        matrix = VRUtil.fromVRMatrix43(m43);

        if(controllerpose.bPoseIsValid() && controllerpose.bDeviceIsConnected() && controllerpose.eTrackingResult() == ETrackingResult_TrackingResult_Running_OK){


            //WorldEngine.getCurrent().find("ballmodel").setPositionOffset(new Vector3f(matrix.m03, matrix.m13, matrix.m23));
            //WorldEngine.getCurrent().find("ballmodel").setRotationOffset(VRUtil.getQuaternionFrom43(m43));
        }

        window.endFrame();
    }

    @Override
    public float getRatio() {
        return 1f;
    }

    @Override
    public boolean shouldClose() {
        return window.shouldClose();
    }

    @Override
    public void destroy() {
        window.destroy();
        VR_ShutdownInternal();
    }

    @Override
    public long getID() {
        return window.getID();
    }

    @Override
    public int getWidth() {
        return recx;
    }

    @Override
    public int getHeight() {
        return recy;
    }

    @Override
    public boolean getSuccessfulConstruction() {
        return window.getSuccessfulConstruction();
    }

    @Override
    public String getType() {
        return "OpenVR";
    }

    @Override
    public void setIcon(String path) throws Exception {
        window.setIcon(path);
    }

    @Override
    public void setVSync(boolean vsync) {
        window.setVSync(vsync);
    }

    @Override
    public void setCurrentContext() {
        window.setCurrentContext();
    }
}
