package com.opengg.core.vr;


import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import org.lwjgl.openvr.*;

import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_LeftHand;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_RightHand;
import static org.lwjgl.openvr.VR.ETrackingUniverseOrigin_TrackingUniverseStanding;

public class VR {
    public static Pose getHMDPose(){
        var hmdRenderBuffers = TrackedDevicePose.calloc(1);
        VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(ETrackingUniverseOrigin_TrackingUniverseStanding,0,hmdRenderBuffers);
        var m43 = hmdRenderBuffers.get(0).mDeviceToAbsoluteTracking();
        hmdRenderBuffers.free();

        return getPoseFromMatrix(m43);
    }

    public static Pose getControllerPose(int index){
        var controllerState = VRControllerState.calloc();
        var controllerPose = TrackedDevicePose.calloc();
        VRSystem.VRSystem_GetControllerStateWithPose(ETrackingUniverseOrigin_TrackingUniverseStanding,3+index, controllerState, 1, controllerPose);
        var m43 = controllerPose.mDeviceToAbsoluteTracking();
        controllerState.free();
        controllerPose.free();

        return getPoseFromMatrix(m43);
    }

    private static Pose getPoseFromMatrix(HmdMatrix34 m43){
        var matrix = VRUtil.fromVRMatrix43(m43);

        var position = new Vector3f(matrix.m03, matrix.m13, matrix.m23);
        var rotation = VRUtil.getQuaternionFrom43(m43);

        return new Pose(position, rotation);
    }


    public enum Controller{
        LEFT(ETrackedControllerRole_TrackedControllerRole_LeftHand),
        RIGHT(ETrackedControllerRole_TrackedControllerRole_RightHand);

        Controller(int internalValue){
            this.internalValue = internalValue;
        }

        private final int internalValue;

        public int getInternalValue() {
            return internalValue;
        }
    }

    public static class Pose{
        Vector3f pos;
        Quaternionf rot;

        public Pose(Vector3f pos, Quaternionf rot) {
            this.pos = pos;
            this.rot = rot;
        }

        public Vector3f getPos() {
            return pos;
        }

        public Quaternionf getRot() {
            return rot;
        }
    }
}
