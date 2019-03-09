package com.opengg.core.vr;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;

import static java.lang.Math.copySign;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

public class VRUtil {
    public static Matrix4f fromVRMatrix43(HmdMatrix34 mat){
        return new Matrix4f(
                  mat.m(4*0 + 0), mat.m(4*0 + 1), mat.m(4*0 + 2), mat.m(4*0 + 3)
                , mat.m(4*1 + 0), mat.m(4*1 + 1), mat.m(4*1 + 2), mat.m(4*1 + 3)
                , mat.m(4*2 + 0), mat.m(4*2 + 1), mat.m(4*2 + 2), mat.m(4*2 + 3),
                0,                  0,                  0,              1);
    }

    public static Matrix4f fromVRMatrix44(HmdMatr2ix44 mat){
        return new Matrix4f(
                  mat.m(4*0 + 0), mat.m(4*0 + 1), mat.m(4*0 + 2), mat.m(4*0 + 3)
                , mat.m(4*1 + 0), mat.m(4*1 + 1), mat.m(4*1 + 2), mat.m(4*1 + 3)
                , mat.m(4*2 + 0), mat.m(4*2 + 1), mat.m(4*2 + 2), mat.m(4*2 + 3)
                , mat.m(4*3 + 0), mat.m(4*3 + 1), mat.m(4*3 + 2), mat.m(4*3 + 3));
    }

    public static Quaternionf getQuaternionFrom43(HmdMatrix34 mat){
        Matrix4f m = fromVRMatrix43(mat);

        float w = (float) (sqrt(max(0, 1 + m.m00 + m.m11 + m.m22))/2);
        float x = (float) (sqrt(max(0, 1 + m.m00 - m.m11 - m.m22))/2);
        float y = (float) (sqrt(max(0, 1 - m.m00 + m.m11 - m.m22))/2);
        float z = (float) (sqrt(max(0, 1 - m.m00 - m.m11 + m.m22))/2);

        x = copySign(x, m.m21 - m.m12);
        y = copySign(y, m.m02 - m.m20);
        z = copySign(z, m.m10 - m.m01);

        return new Quaternionf(w,x,y,z);
    }
}
