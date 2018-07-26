@version 4.2
@glsl define MAX_JOINTS 100

in vec3 normal;
in vec4 weights;
in vec2 texcoord;
in vec4 color;
in vec3 position;
in vec4 jointindex;
            
out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
	
	vec2 textureCoord;
	vec3 pos;
	vec3 norm;
};

uniform mat4 model;
uniform mat4 jointsMatrix[MAX_JOINTS];
uniform mat4 view;
uniform mat4 projection;
uniform vec3 rot;
uniform vec3 lightpos;
uniform mat4 shmvp;
uniform int mode;
uniform int inst;
uniform float divAmount;


const int MAX_WEIGHTS = 4;


void main() {

    mat4 modelView = view * model;
    textureCoord = texcoord;
	vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
	 int count = 0;
        for(int i = 0; i < MAX_WEIGHTS; i++)
        {
            float weight = weights[i];
			int stupidity = int(jointindex[i]+0.01);
            if(stupidity != -100 || weight > 0) {
                count++;
                int jointIndex = int(jointindex[i]+0.01);
                vec4 tmpPos = jointsMatrix[jointIndex] * vec4(position, 1.0);
                initPos += weight * tmpPos;

                vec4 tmpNormal = jointsMatrix[jointIndex] * vec4(normal, 0.0);
                initNormal += weight * tmpNormal;
            }
        }
        if (count == 0)
        {
            initPos = vec4(position, 1.0);
            initNormal = vec4(norm, 0.0);
        }
    pos = (model * initPos).xyz;
	
	
    norm = initNormal.xyz;
	
    vec4 P = view * vec4(pos.xyz,1);
    gl_Position = projection * P;
}