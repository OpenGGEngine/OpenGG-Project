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
uniform vec3 lightpos;
uniform mat4 shmvp;
uniform int mode;
uniform int inst;
uniform float divAmount;

void main() {
    
    if(mode == 6){
        poss = position;
    }
    
    mat4 modelView = view * model;
    if(inst == 1){
        vertexColors = vec4(1,1,1,1);
        textureCoords = texcoord;
        poss = vec3(position.x + color.x, position.y + color.y, position.z + color.z);
        norms = normal;
        if(billboard == 1){
            modelView[0][0] = 1.0; 
            modelView[0][1] = 0.0; 
            modelView[0][2] = 0.0; 
            modelView[2][0] = 0.0; 
            modelView[2][1] = 0.0; 
            modelView[2][2] = 1.0; 
        }
        gl_Position = projection * modelView * vec4(poss, 1.0f);
        return;
    }
    vertexColors = color;
    textureCoords = texcoord;
    poss = vec3(position);
    norms = normal;
    if(billboard == 1){
        modelView[0][0] = 1.0; 
        modelView[0][1] = 0.0; 
        modelView[0][2] = 0.0; 
        modelView[2][0] = 0.0; 
        modelView[2][1] = 0.0; 
        modelView[2][2] = 1.0; 
    }
    vec4 P = modelView * vec4(position,1);
    gl_Position = projection * P;
};