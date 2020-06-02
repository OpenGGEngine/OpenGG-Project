COMPILED GGSL ERROR SOURCE: From shader cuboid_scaling.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:223: error(#172) Too many arguments constructor
ERROR: error(#273) 1 compilation errors.  No code generated
#version 420
uniform mat4 model;
uniform vec3 camera;
uniform mat4 perspective;
uniform mat4 view;
layout(location = 0) out vec4 fcolor;
layout(location = 0) in vec2 textureCoord;
layout(location = 1) in vec3 pos;
layout(location = 2) in vec3 norm;
vec3 n;
uniform layout(binding = 10) sampler2D Kd;
uniform layout(binding = 11) samplerCube cubemap;
vec4 getTex(sampler2D tname) {
    return texture(tname, textureCoord);
} 

#define LIGHTNUM 100
uniform float farplane;
uniform float nearplane;
uniform int shadow;
uniform int numLights;
struct Light {
    vec4 lightpos;
    vec4 color;
    vec4 dir;
    mat4 view;
    mat4 perspective;
    float lightdistance;
    float type;
    float shadow;
    float angle;
} ;

layout(std140, binding = 0) uniform LightBuffer {
    Light lights[LIGHTNUM];
} ;

uniform layout(binding = 2) sampler2D shadowmap0;
uniform layout(binding = 3) sampler2D shadowmap1;
uniform layout(binding = 4) sampler2D shadowmap2;
uniform layout(binding = 5) samplerCube shadowcube;
float pointLight(Light light) {
    vec3 sampleOffsetDirections[20] = vec3[](vec3(1, 1, 1), vec3(1, -1, 1), vec3(-1, -1, 1), vec3(-1, 1, 1), vec3(1, 1, -1), vec3(1, -1, -1), vec3(-1, -1, -1), vec3(-1, 1, -1), vec3(1, 1, 0), vec3(1, -1, 0), vec3(-1, -1, 0), vec3(-1, 1, 0), vec3(1, 0, 1), vec3(-1, 0, 1), vec3(1, 0, -1), vec3(-1, 0, -1), vec3(0, 1, 1), vec3(0, -1, 1), vec3(0, -1, -1), vec3(0, 1, -1));
    float viewDistance = length((camera - pos));
    vec3 fragToLight = (pos - light.lightpos.xyz);
    float currentDepth = (length(fragToLight) / light.lightdistance);
    float shadow = 0.0f;
    float normalBias = 0.005f;
    float depthBias = 8.0E-4f;
    int samples = 20;
    float diskRadius = ((1.0f + (viewDistance / light.lightdistance)) / 25.0f);
    for(int i = 0; (i < samples); i++){
        float closestDepth = texture(shadowcube, ((fragToLight + ((n * normalBias) * abs(dot(n, fragToLight)))) + (sampleOffsetDirections[i] * diskRadius))).r;
        if(((currentDepth - depthBias) > closestDepth)){
            shadow += 1.0f;
        };
    };
    shadow = (shadow / float(samples));
    return shadow;
} 

float directionalLight(Light light) {
    vec3 lightDir = vec3(1);
    if((light.type == 2.0f)){
        lightDir = normalize(-light.dir.xyz);
    }else{
        lightDir = normalize((light.lightpos.xyz - pos.xyz));
    };
    float depthBias = max((0.008f * (1.0f - abs(dot(n, lightDir)))), 5.0E-4f);
    vec4 lightspacePos = (light.perspective * (light.view * vec4(pos, 1.0f)));
    vec3 projCoords = (lightspacePos.xyz / lightspacePos.w);
    projCoords = ((projCoords * 0.5f) + 0.5f);
    float shadow = 0.0f;
    vec2 texelSize = (1.0f / textureSize(shadowmap0, 0));
    for(float x = -1.5f; (x < 1.6f); x++){
        for(float y = -1.5f; (y < 1.6f); y++){
            float pcfDepth = texture(shadowmap0, (projCoords.xy + (vec2(x, y) * texelSize))).r;
            shadow += (((projCoords.z - depthBias) > pcfDepth) ? 1.0f : 0.0f);
        };
    };
    shadow /= 16.0f;
    return shadow;
} 

float LinearizeDepth(float depth) {
    float z = ((depth * 2.0f) - 1.0f);
    return (((2.0f * nearplane) * farplane) / ((farplane + nearplane) - (z * (farplane - nearplane))));
} 

float getShadowCoverage(Light light) {
    if((light.shadow < 0.001f)){
        return 1.0f;
    };
    if((light.type == 1.0f)){
        return pointLight(light);
    }else{
        return directionalLight(light);
    };
} 

struct Material {
    float hasnormmap;
    float hasambmap;
    float hasspec;
    float hasspecpow;
    float hascolormap;
    float hasem;
    vec3 ks;
    vec3 ka;
    vec3 kd;
    float ns;
} ;

uniform layout(binding = 0) sampler2D Ka;
uniform layout(binding = 6) sampler2D Ks;
uniform layout(binding = 7) sampler2D Ns;
uniform layout(binding = 8) sampler2D em;
uniform layout(binding = 9) sampler2D bump;
vec3 ambient;
vec3 specular;
vec3 diffuse;
vec3 emmisive;
vec4 color;
float specpow;
vec3 specpowvec;
vec3 reflectedcolor;
float trans;
mat3 cotangent_frame(vec3 N, vec3 p, vec2 uv) {
    vec3 dp1 = dFdx(p);
    vec3 dp2 = dFdy(p);
    vec2 duv1 = dFdx(uv);
    vec2 duv2 = dFdy(uv);
    vec3 dp2perp = cross(dp2, N);
    vec3 dp1perp = cross(N, dp1);
    vec3 T = ((dp2perp * duv1.x) + (dp1perp * duv2.x));
    vec3 B = ((dp2perp * duv1.y) + (dp1perp * duv2.y));
    float invmax = inversesqrt(max(dot(T, T), dot(B, B)));
    return mat3((T * invmax), (B * invmax), N);
} 

vec3 calculatenormal(vec3 N, vec3 V, vec3 map, vec2 texcoord) {
    map = (((map * 255.0f) / -128.0f) / 127.0f);
    mat3 TBN = cotangent_frame(N, -V, texcoord);
    return normalize((TBN * map));
} 

void useMaterial(Material mat, vec4 color) {
    diffuse = color.rgb;
    emmisive = (getTex(em).xyz * mat.hasem);
    trans = color.a;
    ambient = (0.3f * diffuse);
    specular = ((getTex(Ks).xyz * mat.hasspec) + (mat.ks * (1 - mat.hasspec)));
    specpow = (((getTex(Ns).r * mat.hasspecpow) * 128) + (mat.ns * (1 - mat.hasspecpow)));
    n = (normalize(norm) * (1 - mat.hasnormmap));
} 

void useMaterial(Material mat) {
    useMaterial(mat, getTex(Kd));
} 

float bloomMin = 0.9f;
float vis = 1;
vec3 eyedir;
void generatePhongData() {
    eyedir = normalize((camera - pos.xyz));
} 

vec3 getPhongFrom(Light light) {
    float attenuation = 1.0f;
    vec3 lightDir = normalize(-light.dir.xyz);
    if((light.type == 2.0f)){
    }else{
        float distance = length((light.lightpos.xyz - pos.xyz));
        attenuation = (1.0f / (1.0f + (((1.0f / light.lightdistance) * distance) * distance)));
        lightDir = normalize((light.lightpos.xyz - pos.xyz));
    };
    vec3 halfwayDir = normalize((lightDir + eyedir));
    float cosTheta = max(dot(n, lightDir), 0.0f);
    vec3 fdif = (diffuse * cosTheta);
    float cosAlpha = max(dot(n, halfwayDir), 0.0f);
    vec3 fspec = (specular * pow(cosAlpha, specpow));
    float shadowcover = 0;
    vec3 fragColor = ((((fdif + fspec) * attenuation) * (1 - shadowcover)) * light.color.rgb);
    return fragColor;
} 

uniform float cuboidscaling;
uniform vec3 cuboidscale;
void main() {
    vec2 newCoord = textureCoord;
    if((cuboidscaling != 0.0f)){
        vec3 untranformedNormal = normalize(vec3((inverse(model) * vec4(norm, 0.0f))));
        if((untranformedNormal.x != 0.0f)){
            newCoord = (vec2(cuboidscale.y, cuboidscale.z) * textureCoord);
        }else{
            if((untranformedNormal.y != 0.0f)){
                newCoord = (vec2(cuboidscale.x, cuboidscale.z) * textureCoord);
            }else{
                if((untranformedNormal.z != 0.0f)){
                    newCoord = (vec2(cuboidscale.x, cuboidscale.y) * textureCoord);
                };
            };
        };
    };
    generatePhongData();
    Material material;
    material.hasnormmap = 0.0f;
    material.hasambmap = 0.0f;
    material.hasspec = 0.0f;
    material.hasspecpow = 0.0f;
    material.hascolormap = 1.0f;
    material.hasem = 0.0f;
    material.ks = vec3(0, 0, 0);
    material.ka = vec3(0, 0, 0);
    material.kd = vec3(0, 0, 0);
    material.ns = 128;
    useMaterial(material, texture(Kd, newCoord));
    vec3 col = ambient;
    col += emmisive;
    for(int i = 0; (i < numLights); i++){
        col += getPhongFrom(lights[i]);
    };
    fcolor = vec4(texture(Kd, textureCoord), trans);
} 

