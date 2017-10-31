#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 lightdir;
    vec3 eyedir;
    vec4 pos;
    vec3 norm;
    vec4 shadowpos;
    float visibility;
};

uniform sampler2D Kd;
uniform sampler2D Ka;

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

void main() {   
	vec4 color1 = getTex(Kd);
	vec4 color2 = getTex(Ka);
	
	color1 = color1 + color2;
	
    fcolor = color1;
	fcolor = getTex(Kd);
}
