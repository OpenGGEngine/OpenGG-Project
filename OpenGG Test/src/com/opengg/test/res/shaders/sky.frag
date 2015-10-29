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
    
    vec4 vertcolor = vertexColor;
    
    vec3 npos = normalize(pos);
    
    vec4 color = textureCube(skyTex, pos);

/*
    if(texture2D(texImage,textureCoord).a == 0){
            color = vertcolor;
            color.a = 0;
    }
*/

    fragColor = color;
    color = fragColor;
};

