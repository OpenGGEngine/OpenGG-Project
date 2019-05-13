COMPILED GGSL ERROR SOURCE: From shader cubemap.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:19: error(#198) Redefinition error: model
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

uniform mat4 model;
vec4 getCube() {
return texture(cubemap, normalize((model * vec4(pos.xyz, 1)).xyz));
} 

void main() {
fcolor = getCube();
} 

