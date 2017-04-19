#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

uniform sampler2D Kd;

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}
void main() {   
    fcolor = getTex(Kd);
};
