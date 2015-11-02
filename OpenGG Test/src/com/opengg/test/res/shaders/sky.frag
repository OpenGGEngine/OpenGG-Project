#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out vec3 color2;

in vec4 vertexColor;
in vec3 pos;

out vec4 fragColor;

uniform float lightdistance;
uniform float lightpower;
uniform samplerCube skyTex;
void main() {	

    vec3 npos = normalize(pos);
    
    vec4 color = texture(skyTex, npos);

    fragColor = color;
    color = fragColor;
};

