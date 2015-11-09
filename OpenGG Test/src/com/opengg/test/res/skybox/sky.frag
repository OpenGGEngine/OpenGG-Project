#version 330 core

layout(location = 0) out vec4 color;
layout(location = 1) out vec3 color2;

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 lightdir;
in vec3 eyedir;
in vec4 pos;
in vec3 norm;
in vec3 lightposition;

out vec4 fragColor;

uniform float lightdistance;
uniform float lightpower;
uniform samplerCube skyTex;
void main() {

        vec4 vertcolor = vertexColor;

	vec4 diffuse = texture(skyTex, normalize(pos.xyz));

	fragColor = diffuse;
	color = fragColor;
};




