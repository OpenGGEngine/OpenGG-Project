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
uniform int mode;
uniform int inst;
uniform float divAmount;

void main() {
    if(inst == 1){
        vertexColors = vec4(1,1,1,1);
        textureCoords = texcoord;
        poss = vec3(position.x + color.x, position.y + color.y, position.z + color.z);
        norms = normal;
        gl_Position = projection * view * model * vec4(poss, 1.0f);
        return;
    }
    vertexColors = color;
    textureCoords = texcoord;
    poss = vec3(position);
    norms = normal;
    
    gl_Position = projection * view * model * vec4(position, 1.0f);
    
};