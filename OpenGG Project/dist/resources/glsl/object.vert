#version 420 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;
            
out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
	
	vec2 textureCoord;
	vec3 pos;
	vec3 norm;
};

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {

    mat4 modelView = view * model;

    textureCoord = texcoord;
    norm = normal;
	
    pos = (model * vec4(position, 1.0f) ).xyz;
    vec4 P = view * vec4(pos, 1.0f);
    gl_Position = projection * P;
}