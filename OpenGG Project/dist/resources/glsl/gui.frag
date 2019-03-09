@version 4.2


layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform int  text;


vec4 getTex(sampler2D tname){
    if(text == 1){
        vec4 col = texture(tname, textureCoord);
        float width = 0.4f;
        float edge = 0.2f;
        float dist = 1-col.a;
        float alpha = 1-smoothstep(width,width+edge,dist);
        vec3 colr = col.rgb;
        return vec4(colr, alpha);
    }
	
    return texture(tname, textureCoord);
}
void main() {
    fcolor = getTex(Kd);
    //fcolor = vec4(fcolor.a);
	if(fcolor.a < 0.1f)
		discard;
}
