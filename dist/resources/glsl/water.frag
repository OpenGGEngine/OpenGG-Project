@version 4.2
@glsl define LIGHTNUM 100


layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};
struct Light
{ 
    vec3 lightpos;
    vec3 color;
	float lightdistance;
	float lightdistance2;
};

layout(std140) uniform LightBuffer {
	Light lights[LIGHTNUM];
};

uniform float uvmultx;
uniform float uvoffsetx;
uniform float uvoffsety;
uniform int numLights;
uniform mat4 view;
uniform mat4 model;
uniform vec3 camera;
uniform sampler2D Kd;
uniform samplerCube cubemap;


float trans;
float specpow;
vec3 eyedir;
vec3 reflectedcolor;
vec3 n;
vec3 ambient;
vec3 specular;
vec3 diffuse;
vec4 color;
vec4 finalcolor;


void genPhong(){
    eyedir = normalize(camera - pos.xyz);

    float distance = length(camera - pos.xyz);
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

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord * uvmultx + vec2(uvoffsetx, uvoffsety));
}

void process(){
    diffuse = getTex(Kd).rgb;

    ambient = 0.2f * diffuse;
    
    specpow = 150;

    specular = vec3(0.6f,0.6f,1);
    
	n = normalize(( model * vec4(norm,0.0f)).xyz);

	reflectedcolor = texture(cubemap, normalize(reflect(eyedir,n))).xyz;
	
	ambient = ambient * reflectedcolor;
}

void main() {  
	genPhong();
	process();
	vec3 col = vec3(0,0,0);
	
	for(int i = 0; i < numLights; i++){
		col += shadify(lights[i]);
	}

	fcolor = vec4(col + ambient, 0.7f);
}
