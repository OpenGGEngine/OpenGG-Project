@version 4.2
@glsl define LIGHTNUM 100
@include phong.ggsl
@include stdfrag.ggsl

@fields
layout (std140) uniform LightBuffer {
	Light lights[LIGHTNUM];
};

uniform int numLights;
uniform sampler2D shadowmap;
uniform sampler2D shadowmap2;
uniform sampler2D shadowmap3;

uniform Material material;

float trans;


@code
float getShadowPercent(Light light, int i){
    vec4 lightspacePos = light.perspective*(light.view * vec4(pos, 1.0f));
    vec3 projCoords = lightspacePos.xyz;///lightspacePos.w;
    projCoords = projCoords * 0.5f + 0.5f;
    float closestDepth = texture(shadowmap, projCoords.xy).r;

    float bias = 0.005*tan(acos(dot(norm, light.lightpos.xyz))); // cosTheta is dot( n,l ), clamped between 0 and 1
    bias = clamp(bias, 0,0.01);
    bias = 0.008f;

    float shadow = (projCoords.z -0.5f) - bias > closestDepth  ? 0.0f : 1.0f;
    return shadow;
}

main() {
    generatePhongData();
    useMaterial(material);
    vec3 col = vec3(0,0,0);//ambient;
    int w = 0;
    //for(int i = 0; i < numLights; i++){
        col += getPhongFrom(lights[0]) * getShadowPercent(lights[0], 0);
        w++;
    //}
    
    //fcolor = vec4(col, trans);
    //fcolor = vec4(vec3(getShadowPercent(lights[0], 0)), trans);
    fcolor = vec4(col + ambient, trans);
}

