package com.opengg.core.vr;


import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;

import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_LeftHand;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_RightHand;
import static org.lwjgl.openvr.VR.ETrackingUniverseOrigin_TrackingUniverseStanding;

public class GGVR {

    public static Tuple<Vector3f, Quaternionf> getHMDPose(){
        var hmdRenderBuffers = TrackedDevicePose.calloc(1);
        VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(ETrackingUniverseOrigin_TrackingUniverseStanding,0,hmdRenderBuffers);
        var m43 = hmdRenderBuffers.get(0).mDeviceToAbsoluteTracking();
        var matrix = VRUtil.fromVRMatrix43(m43);

        var position = new Vector3f(matrix.m03, matrix.m13, matrix.m23);
        var rotation = VRUtil.getQuaternionFrom43(m43);

        hmdRenderBuffers.free();

        return new Tuple<>(position, rotation);
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
}
