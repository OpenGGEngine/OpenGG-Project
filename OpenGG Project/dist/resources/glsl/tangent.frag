@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

in tangentData{
    vec3 tan;
};

uniform Material material;

void main() {
	generatePhongData();
	useMaterial(material);

    if(trans == 0.1f) discard;

    vec3 T = normalize(vec3(model * vec4(tan,   0.0)));
    vec3 N = normalize(vec3(model * vec4(norm,    0.0)));
    vec3 B = normalize(cross(T,N));

    mat3 TBN = mat3(T, B, N);

	n = texture(bump, textureCoord).rgb * material.hasnormmap + norm * (1-material.hasnormmap);
    n = normalize(n * 2.0 - 1.0);
    n = normalize(TBN * n);

	vec3 col = ambient;
	col += emmisive;

	for(int i = 0; i < numLights; i++){
		col += getPhongFrom(lights[i]);
	}

	fcolor = vec4(col, trans);
}
