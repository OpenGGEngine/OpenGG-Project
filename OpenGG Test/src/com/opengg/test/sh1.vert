#version 330 core
in vec3 color;
in vec2 texcoord;
in vec3 position;
            
out vec3 vertexColor;
out vec2 textureCoord;
            
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float xrot;
uniform float yrot;
uniform float zrot;
void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 mvp = projection * view * model;
	float zrotm = zrot;
	float xrotm = xrot;
	float yrotm = yrot;
	mat4 rotX = mat4(1f,0,0,0,0,cos(xrotm),-sin(xrotm),0,0,sin(xrotm),cos(xrotm),0,0,0,0,1f);
	mat4 rotY = mat4(cos(yrotm),0,sin(yrotm),0,0,1,0,0,-sin(yrotm),0,cos(yrotm),0,0,0,0,1);
	mat4 rotZ = mat4(cos(zrotm), -sin(zrotm),0,0,sin(zrotm),cos(zrotm),0,0,0,0,1,0,0,0,0,1);
	mat4 finRotPosMatrix = rotZ*rotY*rotX;
	vec3 finRotPos = vec3(0,0,0);
	//finRotPos = finRotPos * finRotPosMatrix;

	gl_Position = mvp * (finRotPosMatrix*vec4(position, 1.0));


};