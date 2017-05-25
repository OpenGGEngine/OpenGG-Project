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
uniform int billboard;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform int mode;
uniform float divAmount;

void main() {

    mat4 modelView = view * model;
    vertexColors = color;
    textureCoords = texcoord;
	
	poss = ( model * vec4(position.x + color.x, position.y + color.y, position.z + color.z, 1) ).xyz;
	
    norms = normal;
	
    vec4 P = view * vec4(poss.xyz,1);
    gl_Position = projection * P;
};