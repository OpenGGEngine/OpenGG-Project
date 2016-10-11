#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices=18) out;

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
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

const float density =0.00137;
const float gradient = 2.32;

float epsilon = 0.0009;

void genPhong(int vertNum){

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

void emitInfinityQuad(vec3 p1, vec3 p2){
    mat4 mvp = projection * view * model;
    
    vec3 lightdir = normalize(p1 - light.lightpos);
    gl_Position = mvp * vec4((p1 + lightdir * epsilon), 1.0);
    EmitVertex();
    
    gl_Position = mvp * vec4(lightdir, 0.0);
    EmitVertex();
    
    lightdir = normalize(p2 - light.lightpos);
    gl_Position = mvp * vec4((p2 + lightdir * epsilon), 1.0);
    EmitVertex();
    
    gl_Position = mvp * vec4(lightdir, 0.0);
    EmitVertex();

    EndPrimitive();
}

void volume(){
    mat4 mvp = projection * view * model;
    vec3 lightdir;
    if(dot(norms[0], light.lightpos - poss[0]) > 0){
        emitInfinityQuad(poss[0], poss[1]);
        emitInfinityQuad(poss[1], poss[2]);
        emitInfinityQuad(poss[2], poss[0]);
        
        
        lightdir = normalize(poss[0] - light.lightpos); 
        gl_Position = mvp * vec4((poss[0] + light.lightpos * epsilon), 1.0);
        EmitVertex();
        
        lightdir = normalize(poss[1] - light.lightpos); 
        gl_Position = mvp * vec4((poss[1] + light.lightpos * epsilon), 1.0);
        EmitVertex();
        
        lightdir = normalize(pos[2] - light.lightpos); 
        gl_Position = mvp * vec4((poss[2] + light.lightpos * epsilon), 1.0);
        EmitVertex();
        EndPrimitive();
        
        
        lightdir = poss[0] - light.lightpos; 
        gl_Position = mvp * vec4(light.lightpos, 0.0);
        EmitVertex();
        
        lightdir = poss[1] - light.lightpos; 
        gl_Position = mvp * vec4(light.lightpos, 0.0);
        EmitVertex();
        
        lightdir = pos[2] - light.lightpos; 
        gl_Position = mvp * vec4(light.lightpos, 0.0);
        EmitVertex();
        EndPrimitive();
        
    }
}

void main(){
    if(mode == 6){
        volume();
    }
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
