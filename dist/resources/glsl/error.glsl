COMPILED GGSL ERROR SOURCE: From shader object.vert with error code 0: Vertex info
-----------
0(21) : error C7593: Builtin block member gl_PointSize not found in redeclaration of out gl_PerVertex
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
} ;

layout(location = 4) in vec3 position;
layout(location = 0) in vec3 normal;
layout(location = 2) in vec2 texcoord;
void main() {
    textureCoord = texcoord;
    norm = normalize(vec3((model * vec4(normal, 0.0f))));
    pos = (model * vec4(position, 1.0f)).xyz;
    gl_Position = ((projection * view) * vec4(pos, 1.0f));
    gl_PointSize = 0.1f;
} 

