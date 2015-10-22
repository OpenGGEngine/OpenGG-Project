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
uniform sampler2D texImage;
void main() {
	
	vec3 lightcol = vec3(1,1,1);
	float lightpower = 1000f;
	
        vec4 vertcolor = vertexColor;
        
	float lightdistance = 4;
	
	float amb = 0.2;
	
	vec3 diffuse = texture2D(texImage, textureCoord).rgb;
	
	if(texture2D(texImage,textureCoord).a == 0){
		diffuse = vertcolor.rgb;
                vertcolor.a = 0;
	}
	vec3 ambient = vec3(amb,amb,amb) * diffuse;
	vec3 specular = vec3(0.2,0.2,0.2);
	
	float distance = length( lightposition - vec3(pos.x,pos.y,pos.z) );
	
	vec3 n = normalize( norm );
	// Direction of the light (from the fragment to the light)
	vec3 l = normalize( lightdir );
	
	float cosTheta = clamp( dot( n,l ), 0,1 );
	
	vec3 E = normalize(eyedir);
	
	vec3 R = reflect(-l,n);
	
	float cosAlpha = clamp( dot( E,R ), 0,1 );
	
	
	
	fragColor = 
		// Ambient : simulates indirect lighting
		vec4((ambient +
		// Diffuse : "color" of the object
		diffuse * lightcol * lightpower * cosTheta / ((distance*distance)/lightdistance) +
		// Specular : reflective highlight, like a mirror
		specular * lightcol * lightpower * pow(cosAlpha,5) / ((distance*distance)/lightdistance)), vertcolor.a);
	color = fragColor;
};


