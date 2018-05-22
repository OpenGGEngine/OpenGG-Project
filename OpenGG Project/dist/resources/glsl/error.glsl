COMPILED GGSL ERROR SOURCE: From shader object.geom with error code 0: WARNING: 0:2: 'primitive type' : redundant primitive type layout qualifier 
ERROR: 0:3: 'out' : syntax error syntax error
#version 420 core
layout(triangles) layout(triangles) in;
layout(triangle_strip, max_vertices=3) lay out;
in gl_PerVertex {
	vec4 gl_Position;
 float gl_PointSize;
 float gl_ClipDistance[];
};
out vertexData {
	vec2 textureCoord;
 vec4 pos;
 vec3 norm;
};
in vec2 textureCoords[];
in vec3 poss[];
in vec3 norms[];
uniform int mode;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
void main(){
	for(int i = 0; i < gl_in.length(); i++){
 vec4 temppos = gl_in[i].gl_Position;
		
 textureCoord = textureCoords[i];
		norm = norms[i];
 pos = vec4(poss[i], 1.0f);
 gl_Position = temppos;
		
 EmitVertex();

 }
}
