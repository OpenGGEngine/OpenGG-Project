#version 330 core
in vec2 texcoord;
in vec3 normal;

in vec4 color;

in vec3 position;
            
out vec4 vertexColor;
out vec2 textureCoord;
out vec3 lightdir;
out vec3 eyedir;
out vec4 pos;
out vec3 norm;
out vec3 lightposition;
out vec4 shadowpos;
out float visibility;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;
uniform mat4 shmvp;
uniform float divAmount;


const float density =0.00137;
const float gradient = 2.32;

void main() {   
    vertexColor = color;
    textureCoord = texcoord;
    pos = vec4(position, 1.0);
    vec4 worldPosition = model * pos;
    vec4 positionRelativeToCam = view * worldPosition;
    //mat4 mvp = projection * view * model;
	
    //vec3 position2 = vec3(1,1,1);
    
    gl_Position = projection * positionRelativeToCam;
    
    
   
    
   
    shadowpos = (shmvp * pos);
    
    shadowpos = vec4(shadowpos.xyz/shadowpos.w, 1);

    float distance = length(positionRelativeToCam.xyz);
   
    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);
    
  
    shadowpos.x = 0.5 * shadowpos.x + 0.5;
    shadowpos.y = 0.5 * shadowpos.y + 0.5;
    shadowpos.z = 0.5 * shadowpos.z + 0.5;
    
    lightposition = lightpos;

    vec3 posCameraspace = ( positionRelativeToCam).xyz;
    eyedir = vec3(0,0,0) - posCameraspace;

    vec3 lightposCamera = ( view * vec4(lightpos,1)).xyz;
    lightdir = lightposCamera + eyedir;

    norm = ( model * view *  vec4(normal,0)).xyz;
    
    
};