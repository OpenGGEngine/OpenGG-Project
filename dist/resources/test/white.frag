#version 420 core

layout(location=0) out vec4 color;

layout(location=0) in vec2 uv;

layout(binding=4, set=1) uniform sampler2D samplerMan;

void main(void) {
    color = vec4(texture(samplerMan, uv).rgb,1);
}