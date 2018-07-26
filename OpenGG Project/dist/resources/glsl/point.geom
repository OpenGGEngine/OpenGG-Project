@version 4.2

layout(triangles) in;
layout(triangle_strip, max_vertices=18) out;

in gl_PerVertex{
  vec4 gl_Position;
} gl_in[];

uniform mat4 shadowMatrices[6];


main()
{
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