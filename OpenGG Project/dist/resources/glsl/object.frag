@version 420
@glsl define LIGHTNUM 100
@include phong.ggsl
@include stdfrag.ggsl

@fields
layout (std140) uniform LightBuffer {
	Light lights[LIGHTNUM];
};

uniform int numLights;
uniform Material material;
float trans;

@code
main() {
	generatePhongData();
	useMaterial(material);
	vec3 col = ambient;
	int w = 0;
	for(int i = 0; i < numLights; i++){
		col += getPhongFrom(lights[i]);
		w++;
	}

	fcolor = vec4(col, trans);
    //fcolor = vec4(ambient,1);
}
