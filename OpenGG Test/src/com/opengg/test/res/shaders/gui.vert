#version 330 core

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;   

out vec4 vertexColor;
out vec2 textureCoord;

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
    gl_Position = mvp * vec4(position, 1.0);

};
