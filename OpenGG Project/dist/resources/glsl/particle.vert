#version 410 core

in vec2 texcoord;
in vec3 normal;
in vec3 offset;
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
uniform int billboard;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform int mode;
uniform float divAmount;
uniform vec3 camera;

void main() {
    textureCoord = texcoord;
	norm = normal;

    vec3 right = vec3(view[0][0], view[1][0], view[2][0]);
	vec3 up = vec3(view[0][1], view[1][1], view[2][1]);
	
	pos = 
		offset +
		right * position.x +
		up * position.y;
	
    vec4 P = view * vec4(pos.xyz, 1);
    gl_Position = projection * P;
}