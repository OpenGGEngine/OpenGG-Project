COMPILED GGSL ERROR SOURCE: From shader bar.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:22: error(#143) Undeclared identifier: percent
ERROR: error(#273) 1 compilation errors.  No code generated
#version 420
layout(location=0) out vec4 fcolor;
in vertexData {
vec2 textureCoord;
vec3 pos;
vec3 norm;
} ;

vec3 n;
uniform vec3 camera;
uniform mat4 view;
uniform mat4 model;
uniform mat4 perspective;
uniform samplerCube cubemap;
vec4 getTex(sampler2D tname) {
return texture(tname, textureCoord);
} 

uniform vec3 fill;
uniform vec3 back;
void main() {
if((textureCoord.x < percent)){
fcolor = vec4(fill, 1);
}else{
fcolor = vec4(back, 1);
}
;
} 

