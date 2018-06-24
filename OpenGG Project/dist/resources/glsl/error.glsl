#version 420 core
layout(triangles)  in;
layout(triangle_strip, max_vertices=18)  out;
uniform mat4 shadowMatrices[6];
in vertexData {
	vec2 textureCoord;
 vec3 pos;
 vec3 norm;
};
void main(){
	for(int face = 0; face < 6; ++face)
 {
 gl_Layer = face; // built-in variable that specifies to which face we render.
 for(int i = 0; i < 3; ++i) // for each triangle's vertices
 {
 vec4 FragPos = vec4(pos[i], 1);
 gl_Position = shadowMatrices[face] * FragPos;
 EmitVertex();
 }
 EndPrimitive();
 }
}
