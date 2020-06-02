#version 420 core

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 uvcoords;


layout(binding=0, set=1) uniform m
{
    mat4 model;
};

layout(binding=0, set=0) uniform vp
{
    mat4 view;
    mat4 projection;

};

layout(location=0) out vec2 uv;


void main(void) {
    uv = uvcoords;
    gl_Position = projection * view * model * vec4(position, 1.0);
}