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
in float visibility;

out vec4 fragColor;

struct Material
{
    bool hasnormmap;
    bool hasspecmap;
    vec3 ks;
    vec3 ka;
    vec3 kd;
    float specexponent;
};
uniform mat4 shmvp;
uniform float lightdistance;
uniform float lightpower;
uniform float uvmultx;
uniform float uvmulty;
uniform sampler2D texImage;
uniform sampler2D shadeImage;
uniform sampler2D specImage;
uniform sampler2D normImage;
uniform samplerCube cubemap;
uniform vec3 lightpos;
uniform Material material;
uniform int mode;
vec2 camerarange = vec2(1280, 960);
vec2 screensize = vec2(1280, 960);
float amb = 0.3;

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

float bias = 0.005;
float vis = 0.5f;


vec3 diffuse = vec3(1,1,1);

float random(vec3 seed, int i){
	vec4 seed4 = vec4(seed,i);
	float dot_product = dot(seed4, vec4(12.9898,78.233,45.164,94.673));
	return fract(sin(dot_product) * 43758.5453);
}

void lightify(){

    int samples = 8;
    //if ( textureProj( shadeImage, shadp.xyw ).z  <  (shadowpos.z-bias)/shadowpos.w ){
    if(!(shadowpos.x < 0 || shadowpos.y < 0 || shadowpos.x > 1 || shadowpos.y > 1)){
        for(int i = 0; i < samples; i++){
            int index = int(16.0*random(pos.xyy, i))%16;
            if(texture(shadeImage, shadowpos.xy - randdisk[index]/1100 ).r < (shadowpos.z - bias)/shadowpos.w){
                vis -= 0.12;
            }else{

            }
        }
    }
}

vec4 shadify(){
    
    vec3 lightcol = vec3(1,1,1);
    vec4 vertcolor = vertexColor;
    vec4 tempdif;
    
    tempdif = texture(texImage, textureCoord * vec2(uvmultx, uvmulty));
    
    
    
    diffuse = tempdif.rgb;
    
    float trans = tempdif.a;
    
    if(texture2D(shadeImage,textureCoord).a == 0){
            
            diffuse = vertcolor.rgb;
            vertcolor.a = 0;
    }

    vec3 ambient = material.ka * diffuse;
    
    vec3 specular = material.ks; 
    
    float expo  = material.specexponent;
    if(material.hasspecmap){
        specular = texture(specImage, textureCoord * vec2(uvmultx, uvmulty)).xyz ;
    }

    float distance = length( lightpos - pos.xyz );

    vec3 normal = norm;

    if(material.hasnormmap){
        normal = texture(normImage, textureCoord * vec2(uvmultx, uvmulty)).xyz;
    }

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
            vis * specular * expo * lightpower * pow(cosAlpha,5) / ((distance*distance)/lightdistance)), trans);
    return fragColor;
}

vec4 getTex(){
    return texture(texImage, textureCoord * vec2(uvmultx, uvmulty));
}
vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}
vec4 genWaveEffect(){
    vec2 texcoord = textureCoord;
    texcoord.x += sin(texcoord.y * 4*2*3.14159) * 0.10;
    return texture(texImage, vec2(texcoord.x, sin(texcoord.y)));
}
float readDepth( in vec2 coord ) {
	return (2.0 * camerarange.x) / (camerarange.y + camerarange.x - texture2D( shadeImage, coord ).x * (camerarange.y - camerarange.x));	
}
 
float compareDepths( in float depth1, in float depth2 ) {
	float aoCap = 1.0;
	float aoMultiplier=100000.0;
	float depthTolerance=0.000;
	float aorange = 100.0;// units in space the AO effect extends to (this gets divided by the camera far range
	float diff = sqrt( clamp(1.0-(depth1-depth2) / ((camerarange.y-camerarange.x)),0.0,1.0) );
	float ao = min(aoCap,max(0.0,depth1-depth2-depthTolerance) * aoMultiplier) * diff;
	return 1-ao;
}
 
vec4 ssao()
{	
	float depth = readDepth( textureCoord );
	float d;
 
	float pw = 1.0 / screensize.x;
	float ph = 1.0 / screensize.y;
 
	float aoCap = 1.0;
 
	float ao = 0.0;
 
	float aoMultiplier=10000.0;
 
	float depthTolerance = 0.0001;
 
	float aoscale=1.0;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	pw*=2.0;
	ph*=2.0;
	aoMultiplier/=2.0;
	aoscale*=1.2;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	pw*=2.0;
	ph*=2.0;
	aoMultiplier/=2.0;
	aoscale*=1.2;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	pw*=2.0;
	ph*=2.0;
	aoMultiplier/=2.0;
	aoscale*=1.2;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y+ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x+pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
 
	d=readDepth( vec2(textureCoord.x-pw,textureCoord.y-ph));
	ao+=compareDepths(depth,d)/aoscale;
        
	ao/=16.0;
        ao = clamp(ao, 0, .5);
	return vec4(1-ao) * texture2D(texImage,textureCoord) * 2;
}

void processPP(){
    color = ssao();
}
void main() {   
    if(mode == 0){
        lightify();
        color = shadify();
    }else if(mode == 1){
        color = shadify();
    }else if(mode == 2){
        color = getTex();
    }else if(mode == 5){
        processPP();
    }else{
        color = getCube();
    }    
};



