#version 330 core

layout(location = 0) out vec4 fragColor;
layout(location = 1) out float fragmentdepth;

in vec4 vcolor;

void main(){
    fragmentdepth = gl_FragCoord.z;
    fragColor = vcolor;
}
