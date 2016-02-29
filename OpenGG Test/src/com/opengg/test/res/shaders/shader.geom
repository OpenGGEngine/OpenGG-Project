#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices=6) out;

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
out vec3 lightposition;
out vec4 shadowpos;
out float visibility;

uniform int mode;
uniform vec3 lightpos;
uniform mat4 shmvp;
uniform mat4 model;
uniform mat4 view;

const float density =0.00137;
const float gradient = 2.32;

void genPhong(int vertNum){
        shadowpos = (shmvp * vec4(poss[vertNum], 1.0f));
    
        shadowpos = vec4(shadowpos.xyz/shadowpos.w, 1.0f);
        
        vec4 worldPosition = model * vec4(poss[vertNum], 1.0f);
        vec4 positionRelativeToCam = view * worldPosition;
        
        float distance = length(positionRelativeToCam.xyz);

        visibility = exp(-pow((distance*density),gradient));
        visibility = clamp(visibility,0.0,1.0);


        shadowpos.x = 0.5 * shadowpos.x + 0.5;
        shadowpos.y = 0.5 * shadowpos.y + 0.5;
        shadowpos.z = 0.5 * shadowpos.z + 0.5;

        lightposition = lightpos;

        vec3 posCameraspace = ( positionRelativeToCam).xyz;
        eyedir = vec3(0,0,0) - posCameraspace;

        vec3 lightposCamera = ( view * vec4(lightpos,1.0f)).xyz;
        lightdir = lightposCamera + eyedir;

        norm = ( model * view *  vec4(norms[vertNum],1.0f)).xyz;

}

void main(){
    for(int i = 0; i < gl_in.length(); i++){
        if(mode != 2){
            genPhong(i);
        }
        vertexColor = vertexColors[i];
        textureCoord = textureCoords[i];
        pos = vec4(poss[i], 1);
        gl_Position = gl_in[i].gl_Position;
        EmitVertex();
    }
    
};