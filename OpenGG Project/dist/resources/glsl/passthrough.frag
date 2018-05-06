#version 420 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

void main() {   
    fcolor = vec4(1,0,0,1);
}
