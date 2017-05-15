#version 410 core
#define LIGHTNUM 100

layout(location = 0) out vec4 fcolor;
layout(location = 1) out vec4 bright;

in vertexData{
    vec4 vertexColor;
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

struct Material
{
    bool hasnormmap;
    bool hasspecmap;
    bool hasspecpow;
    bool hasambmap;
	bool hascolormap;
    vec3 ks;
    vec3 ka;
    vec3 kd;
    float ns;
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
uniform float time;
uniform int text;
uniform mat4 shmvp;
uniform mat4 view;
uniform mat4 model;
uniform float uvmultx;
uniform float uvmulty;
uniform sampler2D Kd;
uniform sampler2D Ka;
uniform sampler2D Ks;
uniform sampler2D Ns;
uniform sampler2D bump;
uniform samplerCube cubemap;
uniform Material material;

const float density =0.00137;
const float gradient = 2.32;

float bloomMin = 0.9;
float vis = 1;

vec4 color;
vec3 diffuse;
float trans;
vec3 ambient;
vec3 specular;
float specpow;
vec3 n;
float visibility = 1f;
vec3 eyedir;

in vec3 reflectedVector;

mat3 cotangent_frame( vec3 N, vec3 p, vec2 uv ){
    // get edge vectors of the pixel triangle
    vec3 dp1 = dFdx( p );
    vec3 dp2 = dFdy( p );
    vec2 duv1 = dFdx( uv );
    vec2 duv2 = dFdy( uv );
 
    // solve the linear system
    vec3 dp2perp = cross( dp2, N );
    vec3 dp1perp = cross( N, dp1 );
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;
 
    // construct a scale-invariant frame 
    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
    return mat3( T * invmax, B * invmax, N );
}

vec3 calculatenormal( vec3 N, vec3 V, vec2 texcoord ){
    vec3 map = texture( bump, texcoord ).xyz;
    map = map * 255./127. - 128./127.;
    mat3 TBN = cotangent_frame( N, -V, texcoord );
    return normalize( TBN * map );
}


void genPhong(){
    vec3 positionRelativeToCam = (view * model * vec4(pos.xyz, 1.0f)).xyz;
    
    vec3 posCameraspace = (positionRelativeToCam);
    eyedir = vec3(0,0,0) - posCameraspace;

    float distance = length(positionRelativeToCam.xyz);

    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);
}

vec4 getTex(sampler2D tname){
    if(text == 1){
        vec4 col = texture(tname, textureCoord);
        float width = 0.4f;
        float edge = 0.2f;
        float dist = 1-col.a;
        float alpha = 1-smoothstep(width,width+edge,dist);
        vec3 colr = col.rgb;
        return vec4(colr, alpha);
    }
    return texture(tname, textureCoord);
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
	if(material.hascolormap){
		color = getTex(Kd);
	}else{
		color = vec4(material.kd, 1);
	}

    diffuse = color.rgb;

    trans = color.a;
    
    if(trans < 0.2) discard;
    
    ambient = 0.2f * diffuse;
    
    specular = vec3(1,1,1);
    
    specpow = 1;
    if(material.hasspecpow){
        vec4 specpowvec = getTex(Ns);
        specpow = specpowvec.r * 32;
    }else{
        specpow = material.ns;
    }
	
    if(material.hasspecmap){
        specular = getTex(Ks).rgb;
    }else{
        specular = material.ks;
    }
	n = normalize(( view * model * vec4(norm,0.0f)).xyz);
    
    if(material.hasnormmap){
       n = calculatenormal(n,eyedir,textureCoord);
    }
}

void main() {   
	genPhong();
	process();
	vec3 col = vec3(0,0,0);
	
	for(int i = 0; i < numLights; i++){
		col += shadify(lights[i]);
	}
	
	//fcolor = vec4(col + ambient, color.a);
	
	float brightness = (fcolor.r + fcolor.g + fcolor.z) / 3.0;
	if(brightness > bloomMin){
		bright = fcolor;
	}else{
		bright = vec4(0,0,0,1);
	}
	float cheating = max(dot(-vec3(1),normalize(norm)),0.0) + 0.3;
	vec4 reflectedColor = texture(cubemap,reflectedVector);
	fcolor = mix(vec4(0.3,1,0.17,1) * cheating,reflectedColor,0.5);
	fcolor  = fcolor +col;
	fcolor.a = 1;

}

