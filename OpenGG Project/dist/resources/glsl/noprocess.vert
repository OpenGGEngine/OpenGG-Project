@version 4.20
@glsl define LIGHTNUM 100

@fields
in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;

out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
	vec2 textureCoord;
	vec3 pos;
	vec3 norm;
};

@code
void main() {
    textureCoord = texcoord;
    pos = position;
    norm = normal;
	gl_Position = vec4(position, 1.0);
}
