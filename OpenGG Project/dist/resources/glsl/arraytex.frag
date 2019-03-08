@version 4.2
@include phong.ggsl

uniform sampler2DArray anim;
uniform int layer;
uniform int invertMultiplier;

void main() {
	generatePhongData();
	Material material;

	material.hasnormmap = 0.0f;
    material.hasambmap = 0.0f;
    material.hasspec = 0.0f;
    material.hasspecpow = 0.0f;
	material.hascolormap = 1.0f;
   	material.hasem = 0.0f;
    material.ks = vec3(1,1,1);
    material.ka = vec3(0,0,0);
    material.kd = vec3(0,0,0);
    material.ns = 128;

    vec4 colorino = vec4(1,1,1,1);
    if(invertMultiplier == -1){
        colorino = texture(anim, vec3(vec2(1-textureCoord.x, textureCoord.y), layer));
    }else{
        colorino = texture(anim, vec3(textureCoord, layer));
    }

	useMaterial(material, colorino);
	if(trans == 0) discard;

	vec3 col = ambient;
	col += emmisive;

	for(int i = 0; i < 1; i++){
		col += getPhongFrom(lights[i]);
	}

	fcolor = vec4(col, trans);
}
