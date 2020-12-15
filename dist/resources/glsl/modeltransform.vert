@version 4.50
@include stdvert.ggsl

in vec2 texcoord;
in vec3 normal;
in vec4 color;
in vec3 position;

void main() {
    textureCoord = texcoord;
    pos = position;
    norm = normal;
	gl_Position = model * vec4(position, 1.0);
}
