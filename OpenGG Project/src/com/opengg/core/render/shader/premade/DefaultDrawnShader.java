/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.shader.premade;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.ShaderProgram;

/**
 *
 * @author Javier
 */
public interface DefaultDrawnShader {
    public ShaderProgram getProgram();
    public void use();
    public void setLightPos(Vector3f pos);
    public void setModel(Matrix4f model);
    public void setProjection(float fov, float aspect, float znear, float zfar);
    public void setOrtho(float left, float right, float bottom, float top, float near, float far);
    public void setFrustum(float left, float right, float bottom, float top, float near, float far);
    public void setView(Matrix4f view);
    public void checkError();
}
