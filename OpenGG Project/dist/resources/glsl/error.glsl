COMPILED GGSL ERROR SOURCE: From shader bright.frag with error code 0: ERROR: 0:20: 'FragColor' : undeclared identifier 
ERROR: 0:20: 'rgb' : vector field selection out of range 
ERROR: 0:20: 'dot' : no matching overloaded function found (using implicit conversion)
#version 420 core
layout(location = 0) out vec4 fcolor;
in vertexData {
	vec2 textureCoord;
 vec3 pos;
 vec3 norm;
};
uniform mat4 view;
uniform mat4 model;
uniform mat4 perspective;
layout(location = 1) out vec4 bright;
uniform sampler2D Kd;
vec4 getTex(sampler2D tname){
	return texture(tname, textureCoord);
}
void main(){
	vec3 val = getTex(Kd).xyz;
 fcolor = vec4(val,1);
 bright = vec4(0,0,0,1);
	if(dot(FragColor.rgb, vec3(0.2126, 0.7152, 0.0722)) > 1f)
 		bright = vec4(val,1);
}
