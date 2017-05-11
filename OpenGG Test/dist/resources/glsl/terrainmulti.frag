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
uniform sampler2DArray Kd;
uniform sampler2D Ka;


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
	 fcolor = wcolor + wcolorr + wcolorg +wcolorb;
	//fcolor = vec4(1);
	};
