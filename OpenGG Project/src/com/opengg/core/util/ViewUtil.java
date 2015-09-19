/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.Matrix4f;
import com.opengg.core.shader.ShaderProgram;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 *
 * @author Javier
 */
public class ViewUtil {
    public static void setPerspective(float fov, float aspect, float znear, float zfar, ShaderProgram program){
        Matrix4f projection = Matrix4f.perspective(fov, aspect, znear, zfar);
        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);
        glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
    }
}
