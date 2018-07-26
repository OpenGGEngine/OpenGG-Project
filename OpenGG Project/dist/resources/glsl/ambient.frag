@version 4.2


layout(location = 0) out vec4 fcolor;

in vertexData{
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};


struct Material
{
    bool hasnormmap;
    bool hasspecmap;
    bool hasspecpow;
    bool hasambmap;
    vec3 ks;
    vec3 ka;
    vec3 kd;
    float ns;
};

uniform Material material;
uniform sampler2D Kd;
uniform sampler2D Ka;


vec2 camerarange = vec2(1280, 960);
vec2 screensize = vec2(1280, 960);


vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
}

void main() { 
	vec4 color = getTex(Kd);  
    vec3 diffuse = color.rgb;

    float trans = color.a;
    
    if(trans < 0.2) discard;
    vec3 ambient = vec3(0.3f, 0.3f, 0.3f);
	if(material.hasambmap){
		ambient = vec3(getTex(Ka).rgb);
	}else{
		//ambient = material.ka;
	}
	
	fcolor = vec4(ambient * diffuse, trans);
}
