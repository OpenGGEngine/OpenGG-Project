@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

layout(location = 3) in vec2 lmcoord;

uniform vec3 vcolor;
uniform sampler2D lightmap;
uniform int useLm;

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
    vec3 col = diffuse;
    if(vcolor.x != -1){
        col = vcolor;
    }
    if(useLm == 1){
        vec2 realCoord = lmcoord;
        vec3 lm = texture(lightmap,realCoord).xyz;
        //col *= lm;
        //col = lm;
    }
	fcolor = vec4(col, trans);
	if(trans < 0.05f) discard;
}
