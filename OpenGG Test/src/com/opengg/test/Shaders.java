/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Javier
 */
public class Shaders {
    static FloatBuffer vertices = BufferUtils.createFloatBuffer(16 * 8);
    static{                vertices.put(-2).put(-2).put(-1f).put(0f).put(1f).put(0f).put(0f).put(0f); 
        vertices.put(2).put(-2).put(-1f).put(0f).put(0f).put(1f).put(1f).put(0f);
        vertices.put(2).put(2).put(-1f).put(1f).put(0f).put(0f).put(1f).put(1f);
        vertices.put(-2).put(2).put(-1f).put(0f).put(0f).put(0f).put(0f).put(1f);
        
        vertices.put(-2).put(-2).put(-3f).put(0f).put(1f).put(0f).put(0f).put(0f); 
        vertices.put(2).put(-2).put(-3f).put(0f).put(0f).put(1f).put(1f).put(0f);
        vertices.put(2).put(2).put(-3f).put(1f).put(0f).put(0f).put(1f).put(1f);
        vertices.put(-2).put(2).put(-3f).put(0f).put(0f).put(0f).put(0f).put(1f);
        
        vertices.put(-2).put(2).put(-3f).put(0f).put(1f).put(0f).put(0f).put(0f); 
        vertices.put(2).put(2).put(-3f).put(0f).put(0f).put(1f).put(1f).put(0f);
        vertices.put(2).put(2).put(-1f).put(1f).put(0f).put(0f).put(1f).put(1f);
        vertices.put(-2).put(2).put(-1f).put(0f).put(0f).put(0f).put(0f).put(1f);
        
        vertices.put(-2).put(-2).put(-3f).put(0f).put(1f).put(0f).put(0f).put(0f); 
        vertices.put(2).put(-2).put(-3f).put(0f).put(0f).put(1f).put(1f).put(0f);
        vertices.put(2).put(-2).put(-1f).put(1f).put(0f).put(0f).put(1f).put(1f);
        vertices.put(-2).put(-2).put(-1f).put(0f).put(0f).put(0f).put(0f).put(1f);
        
        vertices.flip();
        
    }
    public static final CharSequence vertexSource
            = "#version 330 core\n"
            + "\n"
            +"layout(location = 0) in vec3 vertexPosition_modelspace;\n"
            +"layout(location = 1) in vec2 vertexUV;\n"
            + "in vec3 position;\n"
            + "in vec3 color;\n"
            + "\n"
            + "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            + "    vertexColor = color;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    public static final CharSequence fragmentSource
            = "#version 330 core\n"
            + "\n"
            + "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(vertexColor, 1);\n"
            + "}";
    public static final CharSequence vertexTex
            = "#version 330 core\n"
            + "in vec3 color;\n"
            + "in vec2 texcoord;\n"
            + "in vec3 position;\n"
            
            + "out vec3 vertexColor;\n"
            + "out vec2 textureCoord;\n"
            
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "void main() {\n"
            + "    vertexColor = color;"
            + "    textureCoord = texcoord;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    public static final CharSequence fragmentTex
            = "#version 330 core\n"
            + "in vec3 vertexColor;\n"
            + "in vec2 textureCoord;\n"
            + "out vec4 fragColor;\n"
            + "uniform sampler2D texImage;\n"
            + "void main() {\n"
            + "    vec4 textureColor = texture(texImage, textureCoord);\n"
            + "    fragColor = vec4(vertexColor, 1.0) * textureColor;\n"
            + "}";
}
