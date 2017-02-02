#version 410 core

layout(triangles) in;
layout(triangle_strip, max_vertices=18) out;

in gl_PerVertex
{
  vec4 gl_Position;
  float gl_PointSize;
  float gl_ClipDistance[];
} gl_in[];

out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec3 lightdir;
    vec3 eyedir;
    vec4 pos;
    vec3 norm;
    vec4 shadowpos;
    float visibility;
};

in vec4 vertexColors[];
in vec2 textureCoords[];
in vec3 poss[];
in vec3 norms[];

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){

    for(int i = 0; i < gl_in.length(); i++){
        vec4 temppos = gl_in[i].gl_Position;

        vertexColor = vertexColors[i];
        textureCoord = textureCoords[i];
        pos = vec4(poss[i], 1.0f);
        gl_Position = temppos;
        EmitVertex();
    }
    
};
