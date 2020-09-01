@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

layout(location = 3) in vec2 lmcoord;

uniform vec3 vcolor;
uniform sampler2D lightmap;

void main() {
	generatePhongData();
	Material material;

	material.hasnormmap = 0.0f;
    material.hasambmap = 0.0f;
    material.hasspec = 0.0f;
    material.hasspecpow = 0.0f;
	material.hascolormap = 1.0f;
   	material.hasem = 0.0f;
    material.ks = vec3(0,0,0);
    material.ka = vec3(0,0,0);
    material.kd = vec3(0,0,0);
    material.ns = 128;

	useMaterial(material);
    vec3 col = ambient;
    if(vcolor.x != -1){
        col = vcolor;
    }
    //col = texture(lightmap,lmcoord).xyz;

	//col += emmisive;

	for(int i = 0; i < numLights; i++){
		//col += getPhongFrom(lights[i]);
	}
	fcolor = vec4(col, trans);
	if(trans < 0.05f) discard;
}
