#version 330 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;   

out vec4 vertexColor;
out vec3 pos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;
uniform float skyboxSize;
void main() {
    
    vertexColor = color;
    pos = position;
    
    vec3 pos2 = position * skyboxSize;
    
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(pos2, 1.0);

};