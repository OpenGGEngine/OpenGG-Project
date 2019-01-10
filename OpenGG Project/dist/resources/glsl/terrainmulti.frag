@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

uniform sampler2DArray terrain;

void main() {
	vec4 blendMapColor = getTex(Ka);
	float backTextureAmount = 1- (blendMapColor.r + blendMapColor.b +blendMapColor.g);
    vec2 tiledMapEditor = textureCoord * 40;

    vec4 wcolor = texture(terrain, vec3(tiledMapEditor,0)) * backTextureAmount;
    vec4 wcolorr = texture(terrain, vec3(tiledMapEditor,1)) * blendMapColor.r;
    vec4 wcolorg = texture(terrain, vec3(tiledMapEditor,2)) * blendMapColor.g;
    vec4 wcolorb = texture(terrain, vec3(tiledMapEditor,3)) * blendMapColor.b;

	diffuse = (wcolor + wcolorr + wcolorg + wcolorb).xyz;
    specular = vec3(1,1,1);
    specpow = 1024;
    specpowvec = vec3(specpow);

    n = normalize(( vec4(norm, 0.0f) * model).xyz);
	
	generatePhongData();
	vec3 col = diffuse * 0.2f;

	for(int i = 0; i < 1; i++){
		col += getPhongFrom(lights[i]);
	}

	fcolor = vec4(col,1);
}
