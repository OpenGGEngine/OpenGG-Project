COMPILED GGSL ERROR SOURCE: From shader terrainmulti.frag with error code 0: 
Shader Compile Log:
Fragment shader failed to compile with the following errors:
ERROR: 0:58: error(#160) Cannot convert from: "const float" to: "highp int"
ERROR: 0:61: error(#166) Integer expression required: []
ERROR: error(#273) 2 compilation errors.  No code generated
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
uniform samplerCube cube2;
uniform samplerCube cube3;
float getShadowCoverage(Light light) {
if((light.shadow < 0.001f)){
return 1.0f;
}else{
}
;
if((light.type == 1.0f)){
vec3 sampleOffsetDirections[20] = vec3[](vec3(1, 1, 1), vec3(1, -1, 1), vec3(-1, -1, 1), vec3(-1, 1, 1), vec3(1, 1, -1), vec3(1, -1, -1), vec3(-1, -1, -1), vec3(-1, 1, -1), vec3(1, 1, 0), vec3(1, -1, 0), vec3(-1, -1, 0), vec3(-1, 1, 0), vec3(1, 0, 1), vec3(-1, 0, 1), vec3(1, 0, -1), vec3(-1, 0, -1), vec3(0, 1, 1), vec3(0, -1, 1), vec3(0, -1, -1), vec3(0, 1, -1));
float viewDistance = length((camera - pos));
vec3 fragToLight = (pos - light.lightpos.xyz);
float currentDepth = (length(fragToLight) / light.lightdistance);
float shadow = 0.0f;
float bias = 5.0E-4f;
int samples = 20.0f;
float diskRadius = ((1.0f + (viewDistance / light.lightdistance)) / 25.0f);
for(float i = 0; (i < samples); i++){
float closestDepth = texture(cube2, (fragToLight + (sampleOffsetDirections[i] * diskRadius))).r;
if(((currentDepth - bias) < closestDepth)){
shadow = 1.0f;
}else{
}
;
}
;
shadow = (shadow / float(samples));
return shadow;
}else{
vec4 lightspacePos = (light.perspective * (light.view * vec4(pos, 1.0f)));
vec3 projCoords = lightspacePos.xyz;
;
projCoords = ((projCoords * 0.5f) + 0.5f);
float closestDepth = texture(shadowmap, projCoords.xy).r;
vec3 lightDir = normalize((light.lightpos.xyz - pos.xyz));
float bias = max((0.05f * (1.0f - dot(n, lightDir))), 0.005f);
float shadow = ((((projCoords.z - 0.5f) - bias) > closestDepth) ? 0.0f : 1.0f);
return shadow;
}
;
} 

float LinearizeDepth(float depth) {
float z = ((depth * 2.0f) - 1.0f);
return (((2.0f * nearplane) * farplane) / ((farplane + nearplane) - (z * (farplane - nearplane))));
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

void useMaterial(Material mat) {
color = texture(Kd, textureCoord);
diffuse = color.rgb;
emmisive = (texture(em, textureCoord).xyz * mat.hasem);
trans = color.a;
ambient = (0.07f * diffuse);
specular = ((getTex(Ks).xyz * mat.hasspec) + (mat.ks * (1 - mat.hasspec)));
specpow = mat.ns;
specpowvec = ((getTex(Ns).xyz * mat.hasspec) + vec3((mat.ns * (1 - int(mat.hasspec)))));
n = ((calculatenormal(normalize((model * vec4(norm, 0.0f)).xyz), (pos.xyz - camera), texture(bump, textureCoord).xyz, textureCoord) * mat.hasnormmap) + (normalize((model * vec4(norm, 0.0f)).xyz) * (1 - mat.hasnormmap)));
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
vec3 fspec = ((specular * specpowvec) * pow(cosAlpha, specpow));
float shadowcover = getShadowCoverage(light);
vec3 fragColor = (((((fdif + fspec) * attenuation) * shadowcover) * light.color.rgb) + ambient);
return vec3(shadowcover);
} 

uniform sampler2DArray terrain;
void main() {
vec4 blendMapColor = getTex(Ka);
float backTextureAmount = (1 - ((blendMapColor.r + blendMapColor.b) + blendMapColor.g));
vec2 tiledMapEditor = (textureCoord * 40);
vec4 wcolor = (texture(terrain, vec3(tiledMapEditor, 0)) * backTextureAmount);
vec4 wcolorr = (texture(terrain, vec3(tiledMapEditor, 1)) * blendMapColor.r);
vec4 wcolorg = (texture(terrain, vec3(tiledMapEditor, 2)) * blendMapColor.g);
vec4 wcolorb = (texture(terrain, vec3(tiledMapEditor, 3)) * blendMapColor.b);
diffuse = (((wcolor + wcolorr) + wcolorg) + wcolorb).xyz;
specular = vec3(1, 1, 1);
specpow = 1024;
specpowvec = vec3(specpow);
n = normalize((vec4(norm, 0.0f) * model).xyz);
generatePhongData();
vec3 col = (diffuse * 0.2f);
for(int i = 0; (i < 1); i++){
col = getPhongFrom(lights[i]);
}
;
fcolor = vec4(col, 1);
} 

