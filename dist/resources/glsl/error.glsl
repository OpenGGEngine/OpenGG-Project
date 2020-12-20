COMPILED GGSL ERROR SOURCE: From shader color_alpha.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:13: error(#279) Invalid layout qualifier "set"
ERROR: 0:14: error(#279) Invalid layout qualifier "set"
ERROR: error(#273) 2 compilation errors.  No code generated
#version 420
#extension GL_ARB_explicit_uniform_location : require

uniform mat4 model;
uniform vec3 camera;
uniform mat4 perspective;
uniform mat4 view;
layout(location = 0) out vec4 fcolor;
layout(location = 0) in vec2 textureCoord;
layout(location = 1) in vec3 pos;
layout(location = 2) in vec3 norm;
vec3 n;
layout(set = 1, binding = 1) uniform sampler2D Kd;
uniform layout(set = 2, binding = 0) samplerCube cubemap;
vec4 getTex(sampler2D tname) {
    return texture(tname, textureCoord);
} 

uniform vec3 color;
void main() {
    float alpha = texture(Kd, textureCoord).a;
    if((alpha < 0.1f)){
        discard;
    };
    fcolor = vec4(color, alpha);
} 

;
