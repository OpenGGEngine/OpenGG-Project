COMPILED GGSL ERROR SOURCE: From shader object.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:225: error(#143) Undeclared identifier: texCoords
ERROR: 0:225: error(#202) No matching overloaded function found: texture
ERROR: 0:225: error(#160) Cannot convert from: "const float" to: "out highp 4-component vector of vec4"
ERROR: error(#273) 3 compilation errors.  No code generated
#version 420
layout(location=0) out vec4 fcolor;
in vertexData {
vec2 textureCoord;
vec3 pos;
vec3 norm;
} ;

vec3 n;
uniform vec3 camera;
uniform mat4 view;
uniform mat4 model;
uniform mat4 perspective;
uniform samplerCube cubemap;
vec4 getTex(sampler2D tname) {
return texture(tname, textureCoord);
} 

#define LIGHTNUM 100
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

layout(std140) uniform LightBuffer {
Light lights[LIGHTNUM];
} ;

uniform int numLights;
uniform int shadow;
uniform float nearplane;
uniform float farplane;
uniform sampler2D shadowmap;
uniform sampler2D shadowmap2;
uniform sampler2D shadowmap3;
uniform samplerCube shadowcube;
uniform samplerCube cube3;
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
}else{
}
;
}
;
shadow = (shadow / float(samples));
return shadow;
} 

float directionalLight(Light light) {
vec3 lightDir = vec3(1);
if((light.type == 2.0f)){
lightDir = normalize(-light.dir.xyz);
}else{
lightDir = normalize((light.lightpos.xyz - pos.xyz));
}
;
float depthBias = max((0.008f * (1.0f - abs(dot(n, lightDir)))), 5.0E-4f);
vec4 lightspacePos = (light.perspective * (light.view * vec4(pos, 1.0f)));
vec3 projCoords = (lightspacePos.xyz / lightspacePos.w);
projCoords = ((projCoords * 0.5f) + 0.5f);
float shadow = 0.0f;
vec2 texelSize = (1.0f / textureSize(shadowmap, 0));
for(float x = -1.5f; (x < 1.6f); x++){
for(float y = -1.5f; (y < 1.6f); y++){
float pcfDepth = texture(shadowmap, (projCoords.xy + (vec2(x, y) * texelSize))).r;
shadow += (((projCoords.z - depthBias) > pcfDepth) ? 1.0f : 0.0f);
}
;
}
;
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
}else{
}
;
if((light.type == 1.0f)){
return pointLight(light);
}else{
return directionalLight(light);
}
;
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

uniform sampler2D Kd;
uniform sampler2D Ka;
uniform sampler2D Ks;
uniform sampler2D Ns;
uniform sampler2D em;
uniform sampler2D bump;
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
emmisive = (texture(em, textureCoord).xyz * mat.hasem);
trans = color.a;
ambient = (0.05f * diffuse);
specular = ((getTex(Ks).xyz * mat.hasspec) + (mat.ks * (1 - mat.hasspec)));
specpow = (((getTex(Ns).r * mat.hasspecpow) * 128) + (mat.ns * (1 - mat.hasspecpow)));
n = (normalize(norm) * (1 - mat.hasnormmap));
} 

void useMaterial(Material mat) {
useMaterial(mat, texture(Kd, textureCoord));
} 

uniform float uvmultx;
uniform float uvmulty;
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
}
;
vec3 halfwayDir = normalize((lightDir + eyedir));
float cosTheta = max(dot(n, lightDir), 0.0f);
vec3 fdif = (diffuse * cosTheta);
float cosAlpha = max(dot(n, halfwayDir), 0.0f);
vec3 fspec = (specular * pow(cosAlpha, specpow));
float shadowcover = 1;
shadowcover = getShadowCoverage(light);
vec3 fragColor = ((((fdif + fspec) * attenuation) * (1 - shadowcover)) * light.color.rgb);
return fragColor;
} 

void main() {
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
useMaterial(material);
vec3 col = ambient;
col += emmisive;
for(int i = 0; (i < 1); i++){
col += getPhongFrom(lights[i]);
}
;
fcolor = vec4(col, trans);
fcolor = texture(shadowmap, texCoords);
} 

