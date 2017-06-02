#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform sampler2D Ka;

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

void main() {   
	vec4 blur = getTex(Ka);
	
	if(length(blur) > 0.9)
		blur = vec4(0,0,0,1);
	
    fcolor = blur;
}
