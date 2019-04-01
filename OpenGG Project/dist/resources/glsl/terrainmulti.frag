@version 4.2
@include phong.ggsl
@include stdfrag.ggsl

uniform sampler2DArray terrain;
uniform vec3 scale;

void main() {
	vec4 blendMapColor = getTex(Ka);
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.b +blendMapColor.g);
    vec2 tiledMapEditor = vec2(textureCoord.x * scale.x, textureCoord.y * scale.z);

    vec4 wcolor = texture(terrain, vec3(tiledMapEditor,0)) * backTextureAmount;
    vec4 wcolorr = texture(terrain, vec3(tiledMapEditor,1)) * blendMapColor.r;
    vec4 wcolorg = texture(terrain, vec3(tiledMapEditor,2)) * blendMapColor.g;
    vec4 wcolorb = texture(terrain, vec3(tiledMapEditor,3)) * blendMapColor.b;

    vec4 finalColor = wcolor + wcolorr + wcolorg + wcolorb;

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

    useMaterial(material, finalColor);
	
	vec3 col = ambient;

	for(int i = 0; i < 1; i++){
		col += getPhongFrom(lights[i]);
	}

	fcolor = vec4(col,1);
}
