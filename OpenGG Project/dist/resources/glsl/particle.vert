@version 420 



in vec2 texcoord;
in vec3 normal;
in vec3 offset;
in vec3 position;
            
out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
	
	vec2 textureCoord;
	vec3 pos;
	vec3 norm;
};

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;



void main() {
    textureCoord = texcoord;
    norm = normalize(vec3(model * vec4(normal,0.0)));

    vec3 right = vec3(view[0][0], view[1][0], view[2][0]);
	vec3 up = vec3(view[0][1], view[1][1], view[2][1]);
	
	pos = 
		offset +
		right * position.x +
		up * position.y;
	
    vec4 P = view * vec4(pos.xyz, 1);
    gl_Position = projection * P;
}