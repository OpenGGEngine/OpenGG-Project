#version 330 core

in vec3 position;   
in vec3 color; 
in vec3 normal; 
in vec3 texcoord; 

out vec3 pos;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;
uniform float skyboxSize;

void main() {
    selgsdnvaisudhfkakwasldz..c/xslsc.vcx
    pos = normalize(position);
    
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);

};