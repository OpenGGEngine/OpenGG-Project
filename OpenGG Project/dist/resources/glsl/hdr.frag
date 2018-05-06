#version 420 core
layout(location = 0) out vec4 fcolor;

in vertexData{
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

uniform float exposure;
uniform float gamma;

uniform mat4 view;

uniform sampler2D Kd;


void main() {   
    vec3 color = texture(Kd, textureCoord).rgb;
  
    // Exposure tone mapping
    vec3 mapped = vec3(1.0f) - exp(-color * exposure);
    // Gamma correction
    mapped = pow(mapped, vec3(1.0f / gamma));
  
    fcolor = vec4(mapped, 1.0f);
}