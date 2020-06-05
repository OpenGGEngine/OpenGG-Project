package com.opengg.core.render.shader;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.system.Allocator;

public class CommonUniforms {
    public static void setLightPos(Vector3f pos){
        ShaderController.setUniform("light.lightpos", pos);
    }

    /**
     * Sets the model uniform by calculating it from the given position, rotation, and scale values
     * @param position
     * @param rotation
     */
    public static void setPosRotScale(Vector3f position, Quaternionf rotation, Vector3f scale){
        setModel(new Matrix4f().translate(position).rotate(rotation).scale(scale));
    }

    public static void setModel(Matrix4f model){
        ShaderController.setUniform("model", model);
    }

    public static void setTimeMod(float mod){
        ShaderController.setUniform("time", mod);
    }

    public static void setDistanceField(int distfield){
        ShaderController.setUniform("impl", distfield);
    }

    public static void setView(Matrix4f view){
        ShaderController.setUniform("view", view);
    }

    public static void setProjection(Matrix4f proj){
        ShaderController.setUniform("projection", proj);
    }

    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        var proj = Matrix4f.perspective(fov, aspect, znear, zfar);
        ShaderController.setUniform("projection", proj);
    }

    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        var proj = Matrix4f.orthographic(left, right, bottom, top, near, far);
        ShaderController.setUniform("projection", proj);
    }

    public static void setFrustum(float left, float right, float bottom, float top, float near, float far){
        var proj = Matrix4f.frustum(left, right, bottom, top, near, far);
        ShaderController.setUniform("projection", proj);
    }

    public static void setUVCoordinateMultiplierX(float f){
        ShaderController.setUniform("uvmultx", f);
    }

    public static void setUVCoordinateMultiplierY(float f){
        ShaderController.setUniform("uvmulty", f);
    }

    public static void setInstanced(boolean instanced){
        ShaderController.setUniform("inst", instanced);
    }

    public static void passMaterial(Material m){
        if(RenderEngine.getRendererType() == WindowOptions.RendererType.OPENGL){
            ShaderController.setUniform("material.ns", (float) m.nsExponent);
            ShaderController.setUniform("material.ka",m.ka);
            ShaderController.setUniform("material.kd", m.kd);
            ShaderController.setUniform("material.ks", m.ks);
            ShaderController.setUniform("material.hasspec", m.hasspecmap);
            ShaderController.setUniform("material.hasspecpow", m.hasspecpow);
            ShaderController.setUniform("material.hasnormmap", m.hasnormmap);
            ShaderController.setUniform("material.hasambmap", m.hasreflmap);
            ShaderController.setUniform("material.hascolormap", m.hascolmap);
            ShaderController.setUniform("material.hasem", m.hasemm);
        }else {
            var size = 1+3+3+3+1+1+1+1+1+1;
            var nextBuffer = Allocator.alloc(size * Float.BYTES);
            nextBuffer.asFloatBuffer()
                    .put((float) m.nsExponent)
                    .put(m.ka.toFloatArray())
                    .put(m.kd.toFloatArray())
                    .put(m.ks.toFloatArray())
                    .put(m.hasspecmap ? 1 : 0)
                    .put(m.hasspecpow ? 1 : 0)
                    .put(m.hasnormmap ? 1 : 0)
                    .put(m.hasreflmap ? 1 : 0)
                    .put(m.hascolmap ? 1 : 0)
                    .put(m.hasemm ? 1 : 0);
            ShaderController.setUniform("material", nextBuffer);
        }
    }

    public static void setBillBoard(int yes){
        ShaderController.setUniform("billboard", yes);
    }
}
