#version 410 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;
            
out gl_PerVertex{
    vec4 gl_Position;
};

out vec4 vertexColors;
out vec2 textureCoords;
out vec3 poss;
out vec3 norms;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;


void main() {
	
	mat4 modelView = view * model;
	
    vertexColors = color;
    textureCoords = texcoord;
    poss = vec3(position);
    norms = normal;
    vec4 P = modelView * vec4(position,1);
    gl_Position = projection * P;
};