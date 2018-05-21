@version 4.2
@uniforms
layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform sampler2D Ka;
uniform float percent;

@code
vec4 getTex(sampler2D tname){
    
	
    return texture(tname, textureCoord);
}
void main() {   
    vec4 color1 = getTex(Ka);
	vec4 color2 = getTex(Kd);
	if(textureCoord.x < percent){
		fcolor = color1;
	}else{
		fcolor = color2;
	}
	if(fcolor.a < 0.1f)
		discard;
}
