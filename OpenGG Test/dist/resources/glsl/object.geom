#version 410 core

layout(triangles) in;
layout(triangle_strip, max_vertices=3) out;

in gl_PerVertex
{
  vec4 gl_Position;
  float gl_PointSize;
  float gl_ClipDistance[];
} gl_in[];

out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec3 lightdir;
    vec3 eyedir;
    vec4 pos;
    vec3 norm;
    vec4 shadowpos;
    float visibility;
};

in vec4 vertexColors[];
in vec2 textureCoords[];
in vec3 poss[];
in vec3 norms[];

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



void main(){
    for(int i = 0; i < gl_in.length(); i++){
        vec4 temppos = gl_in[i].gl_Position;
 
        genPhong(i);
		
        vertexColor = vertexColors[i];
        textureCoord = textureCoords[i];
        pos = vec4(poss[i], 1.0f);
        gl_Position = temppos;
		
        EmitVertex();
		if(i % 3 == 2){
			EndPrimitive();
		}
    }
    
};
