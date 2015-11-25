#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out vec3 color2;

//in vec4 vertexColor;
in vec3 pos;

out vec4 fragColor;

uniform samplerCube skyTex;
void main() {	

    
    //vec4 color = texture(skyTex, pos);

    fragColor = vec4(1,1,1,1);
    color = vec4(1,1,1,1);
};

