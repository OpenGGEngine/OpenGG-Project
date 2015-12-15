#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out vec3 color2;

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 lightdir;
in vec3 eyedir;
in vec4 pos;
in vec3 norm;
in vec3 lightposition;

out vec4 fragColor;

const float lowerLimit =100.0;
const float upperLimit = 650.0;

uniform float lightdistance;
uniform float lightpower;
uniform samplerCube skyTex;
void main() {

        vec4 vertcolor = vertexColor;

	vec4 diffuse = texture(skyTex, normalize(pos.xyz));
  
        float factor = (pos.y - lowerLimit)/(upperLimit - lowerLimit);
        factor = clamp(factor,0.0,1.0);
	fragColor = diffuse;
        
	color = fragColor;
         color = mix(vec4(0.53,0.53,0.53,1),fragColor,factor);
};




