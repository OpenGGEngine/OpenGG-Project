#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out float fragmentdepth;

out vec4 fragColor;

in vec4 vcolor;

void main(){
    fragmentdepth = gl_FragCoord.z;
    fragColor = vcolor;
    color = fragColor;
}
