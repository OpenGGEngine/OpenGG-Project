#version 410 core

layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform sampler2D Ka;
uniform float exposure;
uniform float gamma;

void main() {   
    vec3 hdrColor = texture(Kd, textureCoord).rgb;
  
    // Exposure tone mapping
    vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
    // Gamma correction 
    mapped = pow(mapped, vec3(1.0 / gamma));
  
    fcolor = vec4(mapped, 1.0);
}
