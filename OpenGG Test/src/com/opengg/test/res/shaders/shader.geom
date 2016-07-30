#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices=3) out;

in vec4 vertexColors[];
in vec2 textureCoords[];
in vec3 poss[];
in vec3 norms[];

out vec4 vertexColor;
out vec2 textureCoord;
out vec3 lightdir;
out vec3 eyedir;
out vec4 pos;
out vec3 norm;
out vec4 shadowpos;
out float visibility;

struct Light
{
    float lightdistance;
    float lightpower;
    vec3 lightpos;
    vec3 color;
};

uniform int mode;
uniform Light light;
uniform mat4 shmvp;
uniform mat4 model;
uniform mat4 view;

const float density =0.00137;
const float gradient = 2.32;

void genPhong(int vertNum){
        
    shadowpos = (shmvp * vec4(poss[vertNum], 1.0f));
    
    shadowpos = vec4(shadowpos.xyz/shadowpos.w, 1.0f);

    shadowpos.x = 0.5 * shadowpos.x + 0.5;
    shadowpos.y = 0.5 * shadowpos.y + 0.5;
    shadowpos.z = 0.5 * shadowpos.z + 0.5;
    
    
    vec3 worldPosition = (model * vec4(poss[vertNum], 1.0f)).xyz;
    vec3 positionRelativeToCam = (view * model * vec4(poss[vertNum], 1.0f)).xyz;
    
    vec3 posCameraspace = ( positionRelativeToCam);
    eyedir = vec3(0,0,0) - posCameraspace;
    
    vec3 lightposCamera = ( view * vec4(light.lightpos,1.0f)).xyz;
    lightdir = lightposCamera + eyedir;

    norm = norms[vertNum];
    
    
    float distance = length(positionRelativeToCam.xyz);

    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);

}

void main(){
    for(int i = 0; i < gl_in.length(); i++){
        vec4 temppos = gl_in[i].gl_Position;
        
        if(mode != 2 && mode != 5){     
            genPhong(i);
        }
        vertexColor = vertexColors[i];
        textureCoord = textureCoords[i];
        pos = vec4(poss[i], 1.0f);
        gl_Position = temppos;
        EmitVertex();
    }
    
};
