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
uniform float percent;

vec4 getTex(sampler2D tname){
    
	
    return texture(tname, textureCoord);
}
void main() {   
    vec4 color1 = getTex(Ka);
	vec4 color2 = getTex(Kd);
	if(textureCoord.x < percent){
		fcolor = color1;
	}else{
		fcolor = color2;
	}
	if(fcolor.a < 0.1f)
		discard;
}
