@version 4.2


in vec3 normal;
in vec4 weights;
in vec2 texcoord;
in vec3 tangent;
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

 const int MAX_WEIGHTS = 4;
 const int MAX_JOINTS = 200;

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


void main() {

    mat4 modelView = view * model;
    textureCoord = texcoord;
	vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
	vec3 initTan = tangent;
	if(initTan.x == 1){
		textureCoord.x = 0;
	}
	/*mat4 BoneTransform = jointsMatrix[uint(jointindex.x)] * jointindex.z +
						 jointsMatrix[uint(jointindex.y)] * jointindex.w +
						 jointsMatrix[uint(weights.x)] * weights.z +
						 jointsMatrix[uint(weights.y)] * weights.w ;*/
	uint index1 = uint(jointindex[0]);
	uint index2 = uint(jointindex[1]);
	uint index3 = uint(jointindex[2]);
	uint index4 = uint(jointindex[3]);


	mat4 BoneTransform = jointsMatrix[index1] *weights[0] +
						 jointsMatrix[index2] * weights[1]+
						 jointsMatrix[index3] *weights[2] +
						 jointsMatrix[index4] *weights[3] ;
						 
	pos = (  model *BoneTransform * vec4(position, 1.0)).xyz ;
	
   
	
	
    norm = normal;
	//norm = vec3(weights.xyz);
	 //pos = ( vec4(pos,1)).xyz;
    vec4 P = view *vec4(pos.xyz,1);
    gl_Position = projection * P;
}