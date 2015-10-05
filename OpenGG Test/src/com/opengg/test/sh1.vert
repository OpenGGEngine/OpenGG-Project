#version 330 core
in vec2 texcoord;
in vec3 normal;

in vec4 color;

in vec3 position;
            
out vec4 vertexColor;
out vec2 textureCoord;
out vec3 lightdir;
out vec3 eyedir;
out vec4 pos;
out vec3 norm;
out vec3 lightposition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
	
    mat4 mvp = projection * view * model;
	
	float zrotm = rot.z;
	float xrotm = rot.x;
	float yrotm = rot.y;
	
	lightposition = lightpos;
	
	mat4 rotX = mat4(1f,0,0,0,0,cos(xrotm),-sin(xrotm),0,0,sin(xrotm),cos(xrotm),0,0,0,0,1f);
	mat4 rotY = mat4(cos(yrotm),0,sin(yrotm),0,0,1,0,0,-sin(yrotm),0,cos(yrotm),0,0,0,0,1);
	mat4 rotZ = mat4(cos(zrotm), -sin(zrotm),0,0,sin(zrotm),cos(zrotm),0,0,0,0,1,0,0,0,0,1);
	mat4 finRotPosMatrix = rotZ*rotY*rotX;
	vec3 finRotPos = vec3(0,0,0);
	//finRotPos = finRotPos * finRotPosMatrix;

	gl_Position = mvp * (finRotPosMatrix*vec4(position, 1.0));
	pos = (finRotPosMatrix*vec4(position, 1.0));
	
	vec3 posCameraspace = ( view * model * vec4(finRotPosMatrix*vec4(position, 1.0))).xyz;
	eyedir = vec3(0,0,0) - posCameraspace;
	
	vec3 lightposCamera = ( view * vec4(lightpos,1)).xyz;
	lightdir = lightposCamera + eyedir;
	
	norm = ( view * model * vec4(normal,0)).xyz;
};