COMPILED GGSL ERROR SOURCE: From shader reverse.vert with error code 0: 
Shader Compile Log:
Vertex shader failed to compile with the following errors:
ERROR: 0:19: error(#160) Cannot convert from: "4-component vector of vec4" to: "highp 3-component vector of vec3"
ERROR: error(#273) 1 compilation errors.  No code generated
#version 420
uniform mat4 model;
uniform vec3 camera;
uniform mat4 projection;
uniform mat4 view;
layout(location = 0) out vec2 textureCoord;
layout(location = 1) out vec3 pos;
layout(location = 2) out vec3 norm;
out gl_PerVertex {
    vec4 gl_Position;
    float gl_PointSize;
} ;

layout(location = 4) in vec3 position;
layout(location = 0) in vec3 normal;
layout(location = 2) in vec2 texcoord;
void main() {
    textureCoord = texcoord;
    vec3 reverse = (model * vec4(position, 1.0f));
    reverse.x = -reverse.x;
    norm = normalize(vec3((model * vec4(normal, 0.0f))));
    pos = reverse.xyz;
    gl_Position = ((projection * view) * vec4(pos, 1.0f));
    gl_PointSize = 3.0f;
} 

