package com.opengg.core.render.vr;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.View;
import com.opengg.core.world.Camera;
import org.lwjgl.openvr.HmdMatrix34;

public class VRView implements View {
    HmdMatrix34 view;
    Vector3f pos;
    Quaternionf rot;

    public VRView(View camera){
        this.pos = camera.getPosition();
        this.rot = camera.getRotation();
    }

    public VRView(HmdMatrix34 view, Vector3f pos, Quaternionf rot){
        this.view = view;
        this.pos = pos;
        this.rot = rot;
    }

    public void setEyeMatrix(HmdMatrix34 view){
        this.view = view;
    }

    @Override
    public Matrix4f getMatrix() {
        var tmp = VRUtil.fromVRMatrix43(view);
        var eyepos = new Vector3f(tmp.m03, tmp.m13, tmp.m23);
        var eyerot = VRUtil.getQuaternionFrom43(view);
        return new Matrix4f().rotate(eyerot).translate(eyepos.inverse()).multiply(new Matrix4f().rotate(rot).translate(pos.inverse()));//.translate(new Vector3f(pos.x + ipd, pos.y, pos.z).inverse());
    }

    @Override
    public Vector3f getPosition() {
        return pos;
    }

    @Override
    public Quaternionf getRotation() {
        return rot;
    }
}
