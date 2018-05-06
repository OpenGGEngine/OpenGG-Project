#version 420 core
#define LIGHTNUM 100

layout(location = 0) out vec4 fcolor;
layout(location = 1) out vec4 bright;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
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
	int shadow;
	mat4 view;
	mat4 perspective;
};

layout (std140) uniform LightBuffer {
	Light lights[LIGHTNUM];
};

uniform int numLights;
uniform int text;
uniform mat4 view;
uniform mat4 model;
uniform float uvmultx;
uniform float uvmulty;
uniform vec3 camera;
uniform sampler2D Kd;
uniform sampler2D Ka;
uniform sampler2D Ks;
uniform sampler2D Ns;
uniform sampler2D bump;
uniform sampler2D shadowmap;
uniform sampler2D shadowmap2;
uniform sampler2D shadowmap3;
uniform samplerCube cubemap;
uniform Material material;

const float density =0.00137;
const float gradient = 2.32;

float bloomMin = 0.9;
float vis = 1;

float trans;
float specpow;
float visibility = 1.0f;
vec3 eyedir;
vec3 reflectedcolor;
vec3 n;
vec3 ambient;
vec3 specular;
vec3 diffuse;
vec4 color;

float getShadowPercent(Light light, int i){
    vec4 lightspacePos = light.perspective*(light.view*vec4(pos, 1.0f));
    vec3 projCoords = lightspacePos.xyz/lightspacePos.w;
    projCoords = projCoords * 0.5 + 0.5;
    float closestDepth = texture(shadowmap, projCoords.xy).r;
    //float closestDepth = texture(shadowmap, textureCoord.xy).r;
    float shadow = projCoords.z > closestDepth  ? 1.0 : 0.0; 
    return lightspacePos.z;
}

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
    eyedir = normalize(camera - pos.xyz);

    float distance = length(camera - pos.xyz);

    visibility = exp(-pow((distance*density),gradient));
    visibility = clamp(visibility,0.0,1.0);
}

vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

vec3 shadify(Light light){
    
    float distance = length( light.lightpos - pos.xyz ); 
    float attenuation =  (1.0 - (distance/light.lightdistance));
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
    //if(material.hascolormap){
        color = getTex(Kd);
    //}else{
    //  color = vec4(material.kd, 1);
    //}

    diffuse = color.rgb;

    trans = color.a;
    
    ambient = 0.1f * diffuse;
    
    specular = vec3(1,1,1);
    
    specpow = 32;
    /*if(material.hasspecpow){
        vec4 specpowvec = getTex(Ns);
        specpow = specpowvec.r * 32;
    }else{
        specpow = material.ns;
    }*/
    
    if(material.hasspecmap){
        specular = getTex(Ks).rgb;
    }else{
        specular = material.ks;
    }
    
    if(material.hasnormmap){
        n = calculatenormal(normalize((model * vec4(norm,0.0f)).xyz),pos.xyz-camera,textureCoord);
    }else{
        n = normalize((model * vec4(norm,0.0f)).xyz);
    }
    
    //reflectedcolor = texture(cubemap, normalize(reflect(eyedir,n))).xyz;
    
}

void main() {   

    genPhong();
    process();
    vec3 col = ambient;
    int w = 0;
    for(int i = 0; i < numLights; i++){
        col += shadify(lights[i]);// * getShadowPercent(lights[i], i);
        w++;
    }
    
    fcolor = vec4(col, trans);
    fcolor = vec4(vec3(getShadowPercent(lights[0], 0)), trans);
    //fcolor = vec4(ambient,1);
}

