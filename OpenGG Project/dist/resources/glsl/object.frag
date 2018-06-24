@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

@fields
uniform Material material;
float trans;

@code
main() {
	generatePhongData();
	useMaterial(material);
	vec3 col = ambient;
	for(int i = 0; i < numLights; i++){
		col += getPhongFrom(lights[i]);
	}

	fcolor = vec4(col, trans);
}
