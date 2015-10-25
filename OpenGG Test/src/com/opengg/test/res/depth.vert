#version 330 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;

out vec4 vcolor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float divAmount;

void main() {   
    vcolor = color;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
};