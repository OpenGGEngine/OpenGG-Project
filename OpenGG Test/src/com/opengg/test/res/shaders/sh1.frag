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
in vec3 shadowpos;

out vec4 fragColor;

uniform mat4 shmvp;
uniform float lightdistance;
uniform float lightpower;
uniform sampler2D texImage;
uniform sampler2D shadeImage;
void main() {
	
	vec3 lightcol = vec3(1,1,1);
	
        vec3 shadp = shadowpos;
        
        vec4 vertcolor = vertexColor;
        
	float bias = 0.01;
        
	float amb = 0.1;
	
	vec3 diffuse = texture(texImage, textureCoord).rgb;
	
/*
	if(texture2D(shadeImage,textureCoord).a == 0){
		diffuse = vertcolor.rgb;
                vertcolor.a = 0;
	}
*/  
       
        if ( !(texture( shadeImage, shadp.xy ).r  < shadowpos.z - bias)){
            vec3 ambient = vec3(amb,amb,amb) * diffuse;
            vec3 specular = vec3(0.5,0.5,0.5);

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
        }else{
            fragColor = vec4(diffuse.r / 2, diffuse.g / 2, diffuse.b / 2, vertcolor.a);
            color = fragColor;
        }
        
	
        
};




