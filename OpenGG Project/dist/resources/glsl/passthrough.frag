@version 4.2

@fields
layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

@code
main() {
    fcolor = vec4(1,0,0,1);
}
