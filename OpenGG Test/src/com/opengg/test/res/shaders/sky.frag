#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out vec3 color2;

//in vec4 vertexColor;
in vec3 pos;


out vec4 fragColor;


const float lowerLimit =5.0;
const float upperLimit = 50.0;

uniform samplerCube skyTex;
void main() {	

   
    vec4 fragColor = texture(skyTex, pos);
    float factor = (pos.y - lowerLimit)/(upperLimit - lowerLimit);

    factor = clamp(factor,0.0,1.0);
   // factor = 1.0;
    //fragColor = vec4(1,1,1,1);
    color = fragColor;
    
    color = mix(vec4(0.53,0.53,0.53,1.0),fragColor,factor);
    color = vec4(1,0.3,0.3,1.0);

};

