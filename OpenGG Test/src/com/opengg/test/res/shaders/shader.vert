#version 330 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;
            
out vec4 vertexColors;
out vec2 textureCoords;
out vec3 poss;
out vec3 norms;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;
uniform mat4 shmvp;
uniform float divAmount;

void main() {   
    vertexColors = color;
    textureCoords = texcoord;
    poss = vec3(position);
    norms = normal;
    vec4 worldPosition = model * vec4(position, 1.0f);
    vec4 positionRelativeToCam = view * worldPosition;
    //mat4 mvp = projection * view * model;
	
    //vec3 position2 = vec3(1,1,1);
    
    gl_Position = projection * positionRelativeToCam;
    
};