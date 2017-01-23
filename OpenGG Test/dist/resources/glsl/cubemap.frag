#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec3 lightdir;
    vec3 eyedir;
    vec4 pos;
    vec3 norm;
    vec4 shadowpos;
    float visibility;
};

uniform samplerCube cubemap;


vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

void main() {   
    fcolor = getCube();
};
