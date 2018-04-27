#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

uniform sampler2D Kd;

void main() {   
    fcolor = texture(Kd, textureCoord);
	if(fcolor.a == 0)
		discard;
}