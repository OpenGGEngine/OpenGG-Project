@version 4.2
layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

float width = 0.5f;
float edge = 0.1f;
uniform sampler2D Kd;

vec4 getTex(sampler2D tname){
        vec4 col = texture(tname, textureCoord);
       
        float dist = 1-col.a;
        float alpha = 1-smoothstep(width,width+edge,dist);
        vec3 colr = col.rgb;
        return vec4(colr, alpha);
}
void main() {
    fcolor = getTex(Kd);
	//fcolor = texture(Kd,textureCoord);
	//if(fcolor.a < 0.1f)
		//discard;
}
