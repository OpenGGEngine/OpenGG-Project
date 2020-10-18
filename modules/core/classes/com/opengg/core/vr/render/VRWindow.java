package com.opengg.core.vr.render;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Executor;
import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.RenderOperation;
import com.opengg.core.render.RenderPass;
import com.opengg.core.vr.VRUtil;
import com.opengg.core.render.window.glfw.GLFWWindow;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;
import com.opengg.core.world.WorldEngine;
import org.lwjgl.openvr.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.openvr.VR.*;

public class VRWindow implements Window {

    private Window window;
    private int recx, recy;
    private int devicecount;
    private int[] devices;

    private int leftController;
    private int rightController;

    @Override
    public void setup(WindowOptions info) {
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

        String driver = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_TrackingSystemName_String, errHandle);
        String model  = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_ModelNumber_String,        errHandle);
        String serial = VRSystem.VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_SerialNumber_String,       errHandle);

        GGConsole.log("Using " + driver + " tracking system, model " + model + " with serial number " + serial);

        int stationCount = 0;
        int controllerCount = 0;

        devices = new int[k_unMaxTrackedDeviceCount-k_unTrackedDeviceIndex_Hmd];

        for(int i = k_unTrackedDeviceIndex_Hmd; i < k_unMaxTrackedDeviceCount; i++){
            if(VRSystem.VRSystem_IsTrackedDeviceConnected(i)){
                int deviceClass = VRSystem.VRSystem_GetTrackedDeviceClass(i);

                if(deviceClass == 4) stationCount++;
                if(deviceClass == 2){
                    controllerCount++;

                }

                //GGConsole.log("Found device " + VRSystem.VRSystem_GetStringTrackedDeviceProperty(deviceClass, ETracked, errHandle));

                devices[i] = deviceClass;
            }
        }

        if(stationCount < 2) GGConsole.warning("Failed to find both base stations, is one disconnected?");

        IntBuffer w = Allocator.allocInt(1);
        IntBuffer h = Allocator.allocInt(1);

        VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);

        recx = w.get(0) * 1;
        recy = h.get(0) * 1;

        GGConsole.log("Rendering at " + recx + " x " + recy);

        info.width = recx;
        info.height = recy;

        window = new GLFWWindow(); window.setup(info);

        Executor.async(() -> {

            float lleft, lright, ltop, lbot;
            float rleft, rright, rtop, rbot;

            FloatBuffer top = Allocator.allocFloat(1), bot = Allocator.allocFloat(1), left = Allocator.allocFloat(1), right = Allocator.allocFloat(1);

            VRSystem.VRSystem_GetProjectionRaw(EVREye_Eye_Left, left, right, top, bot);

            lleft = left.get(0);
            lright = right.get(0);
            ltop = top.get(0);
            lbot = bot.get(0);

            VRSystem.VRSystem_GetProjectionRaw(EVREye_Eye_Right, left, right, top, bot);

            rleft = left.get(0);
            rright = right.get(0);
            rtop = top.get(0);
            rbot = bot.get(0);

            Vector2f tanHalfFov = new Vector2f(
                    Math.max(Math.max(-lleft, lright), Math.max(-rleft, rright)),
                    Math.max(Math.max(-ltop, lbot), Math.max(-rtop, rbot)));

            VRTextureBounds leftbounds = VRTextureBounds.create();

            leftbounds.uMin((0.5f + 0.5f  * lleft / tanHalfFov.x)/2);
            leftbounds.uMax((0.5f + 0.5f  * lright / tanHalfFov.x)/2);
            leftbounds.vMin(0.5f - 0.5f  * lbot / tanHalfFov.y);
            leftbounds.vMax(0.5f - 0.5f  * ltop / tanHalfFov.y);


            VRTextureBounds rightbounds = VRTextureBounds.create();

            rightbounds.uMin((0.5f + 0.5f  * rleft / tanHalfFov.x)/2 + 0.5f);
            rightbounds.uMax((0.5f + 0.5f  * rright / tanHalfFov.x)/2 + 0.5f);
            rightbounds.vMin(0.5f - 0.5f  * rbot / tanHalfFov.y);
            rightbounds.vMax(0.5f - 0.5f  * rtop / tanHalfFov.y);

            recx = (int) (recx / Math.max(leftbounds.uMax() - leftbounds.uMin(), rightbounds.uMax() - rightbounds.uMin())) /2;
            recy = (int) (recy / Math.max(leftbounds.vMax() - leftbounds.vMin(), rightbounds.vMax() - rightbounds.vMin()));

            //leftbounds.set(0f,0f,0.5f,1f);
            //rightbounds.set(0.5f,0,1f,1);

            float aspect = tanHalfFov.x / tanHalfFov.y;
            float fov = (float) (2.0f * Math.atan(tanHalfFov.y) * FastMath.radDeg);

            ProjectionData data = ProjectionData.getCustom(Matrix4f.perspective(fov, aspect, 0.1f, 1000f));

            var paths = List.copyOf(RenderEngine.getRenderPaths());
            RenderEngine.getRenderPaths().clear();
            RenderEngine.addRenderPath(new RenderOperation("vr", () -> {
                data.use();

                var hmdFramebuffer = ((OpenGLRenderer) RenderEngine.renderer).getCurrentFramebuffer();

                VRView view = new VRView(RenderEngine.getCurrentView());
                view.setEyeMatrix(VRSystem.VRSystem_GetEyeToHeadTransform(EVREye_Eye_Left, HmdMatrix34.create()));
                RenderEngine.useView(view);

                hmdFramebuffer.clearFramebuffer();
                hmdFramebuffer.enableRendering(0,0,recx/2, recy);
                paths.forEach(RenderOperation::render);

                RenderEngine.useView(new Camera(RenderEngine.getCurrentView().getPosition(), RenderEngine.getCurrentView().getRotation()));



                view = new VRView(RenderEngine.getCurrentView());
                view.setEyeMatrix(VRSystem.VRSystem_GetEyeToHeadTransform(EVREye_Eye_Right, HmdMatrix34.create()));
                RenderEngine.useView(view);

                hmdFramebuffer.enableRendering(recx/2,0, recx/2, recy);
                paths.forEach(RenderOperation::render);

                RenderEngine.useView(new Camera(RenderEngine.getCurrentView().getPosition(), RenderEngine.getCurrentView().getRotation()));

            }));

            RenderEngine.getRenderPasses().clear();

            RenderEngine.addRenderPass(new RenderPass(true, true, () -> {
            }, (f) -> {
                var vrtexture = Texture.malloc().set(f.getTextures().get(0).getID(), ETextureType_TextureType_OpenGL, EColorSpace_ColorSpace_Linear);

                VRCompositor.VRCompositor_Submit(EVREye_Eye_Left, vrtexture, leftbounds, EVRSubmitFlags_Submit_Default);
                VRCompositor.VRCompositor_Submit(EVREye_Eye_Right, vrtexture, rightbounds, EVRSubmitFlags_Submit_Default);

                vrtexture.free();

            }));
        });

        GGConsole.log("Initialized OpenVR");
    }

    @Override
    public void startFrame(){
        var event = (VREvent) null;
        while(VRSystem.VRSystem_PollNextEvent(event = VREvent.calloc())){
            //System.out.println(event.eventType());
        }

        var poseRenderBuffers = TrackedDevicePose.calloc(devicecount);
        var poseGameBuffers = TrackedDevicePose.calloc(devicecount);
        VRCompositor.VRCompositor_WaitGetPoses(poseRenderBuffers,poseGameBuffers);
        poseRenderBuffers.free();
        poseGameBuffers.free();

        var hmdRenderBuffers = TrackedDevicePose.calloc(1);
        VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(ETrackingUniverseOrigin_TrackingUniverseStanding,0,hmdRenderBuffers);
        var m43 = hmdRenderBuffers.get(0).mDeviceToAbsoluteTracking();
        var matrix = VRUtil.fromVRMatrix43(m43);

        Camera camera = new Camera();
        RenderEngine.useView(camera);
        camera.setPosition(new Vector3f(matrix.m03, matrix.m13, matrix.m23));
        camera.setRotation(VRUtil.getQuaternionFrom43(m43));

        hmdRenderBuffers.free();

        for(int i = 0; i < 2; i++){
            var controllerstate = VRControllerState.calloc();
            var controllerpose = TrackedDevicePose.calloc();
            VRSystem.VRSystem_GetControllerStateWithPose(ETrackingUniverseOrigin_TrackingUniverseStanding,3+i, controllerstate, 1, controllerpose);
            m43 = controllerpose.mDeviceToAbsoluteTracking();
            matrix = VRUtil.fromVRMatrix43(m43);

            Vector3f pos = new Vector3f(matrix.m03, matrix.m13, matrix.m23);

            if(controllerpose.bPoseIsValid() && controllerpose.bDeviceIsConnected() && controllerpose.eTrackingResult() == ETrackingResult_TrackingResult_Running_OK && !pos.equals(new Vector3f(0,0,0))){
                var comp = WorldEngine.getCurrent().findByName(i == 0 ? "box" : "box2").get(0);
                comp.setPositionOffset(pos);
                comp.setRotationOffset(VRUtil.getQuaternionFrom43(m43).invert());
            }
        }


        window.startFrame();
    }

    @Override
    public void endFrame() {
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
