#version 330 core
in vec3 color;
in vec2 texcoord;
in vec3 position;
            
out vec3 vertexColor;
out vec2 textureCoord;
            
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform int rotlevel;
void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1);
};