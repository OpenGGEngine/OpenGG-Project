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
in vec4 shadowpos;

out vec4 fragColor;

uniform mat4 shmvp;
uniform float lightdistance;
uniform float lightpower;
uniform sampler2D texImage;
uniform sampler2DShadow shadeImage;

vec2 randdisk[16] = vec2[]( 
   vec2( -0.94201624, -0.39906216 ), 
   vec2( 0.94558609, -0.76890725 ), 
   vec2( -0.094184101, -0.92938870 ), 
   vec2( 0.34495938, 0.29387760 ), 
   vec2( -0.91588581, 0.45771432 ), 
   vec2( -0.81544232, -0.87912464 ), 
   vec2( -0.38277543, 0.27676845 ), 
   vec2( 0.97484398, 0.75648379 ), 
   vec2( 0.44323325, -0.97511554 ), 
   vec2( 0.53742981, -0.47373420 ), 
   vec2( -0.26496911, -0.41893023 ), 
   vec2( 0.79197514, 0.19090188 ), 
   vec2( -0.24188840, 0.99706507 ), 
   vec2( -0.81409955, 0.91437590 ), 
   vec2( 0.19984126, 0.78641367 ), 
   vec2( 0.14383161, -0.14100790 ) 
);

void main() {
    float vis = 1;

    vec3 lightcol = vec3(1,1,1);

    vec4 shadp = shadowpos;

    vec4 vertcolor = vertexColor;

    float bias = 0.005;

    float amb = 0.3;

    vec3 diffuse = texture(texImage, textureCoord).rgb;

/*
    if(texture2D(shadeImage,textureCoord).a == 0){
            diffuse = vertcolor.rgb;
            vertcolor.a = 0;
    }
*/   
    vec3 ambient = vec3(amb,amb,amb) * diffuse;
    if(!(shadp.x < 0 || shadp.y < 0 || shadp.x > 1 || shadp.y > 1)){
        for(int i = 0; i < 16; i++){
            int index = i;

            vis -= 0.05 * (1- texture( shadeImage, vec3(shadp.xy  - randdisk[i]/700, (shadp.z - bias)/shadp.w)));

        }
}
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
            vis * diffuse * lightcol * lightpower * cosTheta / ((distance*distance)/lightdistance) +
            // Specular : reflective highlight, like a mirror
            vis * specular * lightcol * lightpower * pow(cosAlpha,5) / ((distance*distance)/lightdistance)), vertcolor.a);
    color = fragColor;

};




