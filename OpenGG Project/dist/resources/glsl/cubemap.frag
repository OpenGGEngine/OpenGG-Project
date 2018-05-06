#version 420 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

uniform samplerCube cubemap;


vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

void main() {   
    fcolor = getCube();
}
