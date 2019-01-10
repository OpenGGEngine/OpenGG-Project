@version 4.2

layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform sampler2D Ka;


vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

void main() {
	vec4 color1 = getTex(Kd);
	vec4 color2 = getTex(Ka);
	
	color1 = color1 + color2;
	
    fcolor = color1;
}
