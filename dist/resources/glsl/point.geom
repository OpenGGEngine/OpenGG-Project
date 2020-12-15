@version 4.5

layout(triangles) in;
layout(triangle_strip, max_vertices=18) out;

in gl_PerVertex{
  vec4 gl_Position;
} gl_in[];

layout(location = 0) in vec2 textureCoord[];
layout(location = 1) in vec3 pos[];
layout(location = 2) in vec3 norm[];

out gl_PerVertex
{
  vec4 gl_Position;
  float gl_PointSize;
  float gl_ClipDistance[];
};

layout(location = 5) out vec4 FragPos;

uniform mat4 shadowMatrices[6];

void main()
{
    for(int face = 0; face < 6; face++)
    {
        gl_Layer = face;
        for(int i = 0; i < 3; i++)
        {
            FragPos = vec4(gl_in[i].gl_Position);
            gl_Position = shadowMatrices[face] * FragPos;
            EmitVertex();
        }
        EndPrimitive();
    }
}