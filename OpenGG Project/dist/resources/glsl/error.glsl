COMPILED GGSL ERROR SOURCE: From shader point.geom with error code 0: ERROR: 0:6: '' : storage layout qualifier cannot be used on input/output blocks
#version 420 core
layout(triangles)  in;
layout(triangle_strip, max_vertices=18)  out;
 inout gl_PerVertex{
 vec4 gl_Position;
} gl_in[];
 uniform mat4 shadowMatrices[6];
void main(){
	for(int face = 0; face < 6; ++face)
 {
 gl_Layer = face;
 for(int i = 0; i < 3; ++i)
 {
 vec4 FragPos = gl_in[i].gl_Position;
 gl_Position = shadowMatrices[face] * FragPos;
 EmitVertex();
 }
 EndPrimitive();
 }
}
