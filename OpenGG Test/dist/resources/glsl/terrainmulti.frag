#version 410 core
#define LIGHTNUM 100


layout(location = 0) out vec4 fcolor;

in vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};
struct Light
{ 
    vec3 lightpos;
    vec3 color;
	float lightdistance;
	float lightdistance2;
};

layout (std140) uniform LightBuffer {
	Light lights[LIGHTNUM];
};

uniform int numLights;
uniform mat4 shmvp;
uniform mat4 view;
uniform mat4 model;
uniform vec3 camera;
uniform sampler2DArray Kd;
uniform sampler2D Ka;
uniform samplerCube cubemap;

float trans;
float specpow;
float visibility = 1f;
vec3 eyedir;
vec3 reflectedcolor;
vec3 n;
vec3 ambient;
vec3 specular;
vec3 diffuse;
vec4 color;
vec4 finalcolor;

void genPhong(){
    vec3 positionRelativeToCam = (view * model * vec4(pos.xyz, 1.0f)).xyz;
    
    vec3 posCameraspace = (positionRelativeToCam);
    eyedir = vec3(0,0,0) - posCameraspace;
}

vec3 shadify(Light light){

	float distance = length( light.lightpos - pos.xyz ); 
	float attenuation =  clamp((1.0 - distance/light.lightdistance), 0.0, 1.0);
	attenuation = attenuation * attenuation;

	vec3 lightDir = normalize(light.lightpos - pos.xyz);
	vec3 halfwayDir = normalize(lightDir + eyedir);

    float cosTheta = max(dot( n,lightDir ), 0.0f );
    vec3 fdif = diffuse * light.color * cosTheta * attenuation;
	
    float cosAlpha = clamp(max(dot(n, halfwayDir), 0.0), 0, 1);
	vec3 fspec = specular * light.color * pow(cosAlpha, specpow) * attenuation;
	
    vec3 fragColor = fdif + fspec;
	
	return fragColor;
}

void process(){
    diffuse = finalcolor.rgb;

    ambient = 0.2f * diffuse;
    
    specpow = 1;

    specular = vec3(0);
    
	n = normalize(( model * vec4(norm,0.0f)).xyz);

	reflectedcolor = texture(cubemap, 
		normalize(reflect(pos.xyz - camera, n))).xyz;
	
	//ambient = ambient * reflectedcolor;
}

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

void main() {  
	vec4 blendMapColor = getTex(Ka);
	float backTextureAmount = 1- (blendMapColor.r + blendMapColor.b +blendMapColor.g);
    vec2 tiledMapEditor = textureCoord * 40;
    vec4 wcolor = texture(Kd, vec3(tiledMapEditor,0)) * backTextureAmount;
    vec4 wcolorr = texture(Kd,vec3(tiledMapEditor,1)) * blendMapColor.r;
    vec4 wcolorg = texture(Kd,vec3(tiledMapEditor,2)) *blendMapColor.g;
    vec4 wcolorb = texture(Kd, vec3(tiledMapEditor,3)) *blendMapColor.b;
	finalcolor = wcolor + wcolorr + wcolorg + wcolorb;
	
	genPhong();
	process();
	vec3 col = vec3(0,0,0);
	
	for(int i = 0; i < numLights; i++){
		col += shadify(lights[i]);
	}

	fcolor = vec4(col + ambient, 1);
};
