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
uniform float divAmount;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
	
    mat4 mvp = projection * view * model;
	
    vec3 position2 = vec3(1,1,1);

    position2.x = position.x/divAmount;
    position2.y = position.y/divAmount;
    position2.z = position.z/divAmount;
    
    lightposition = lightpos;

    gl_Position = mvp * vec4(position2, 1.0);
    pos = vec4(position2, 1.0);

    vec3 posCameraspace = ( view * model * vec4(position2, 1.0)).xyz;
    eyedir = vec3(0,0,0) - posCameraspace;

    vec3 lightposCamera = ( view * vec4(lightpos,1)).xyz;
    lightdir = lightposCamera + eyedir;

    norm = ( model * view *  vec4(normal,0)).xyz;
};
