#version 410 core

layout(location = 0) out vec4 fcolor;
layout(location = 1) out vec4 fcolor2;
layout(location = 2) out vec4 fcolor3;
layout(location = 3) out vec4 fcolor4;

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 lightdir;
in vec3 eyedir;
in vec4 pos;
in vec3 norm;
in vec3 lightposition;
in vec4 shadowpos;
in float visibility;


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
struct Light
{
    float lightdistance;
    float lightpower;
    vec3 lightpos;
    vec3 color;
};

uniform float time;
uniform int text;
uniform mat4 shmvp;
uniform mat4 view;
uniform mat4 model;
uniform float uvmultx;
uniform float uvmulty;
uniform sampler2D Kd;
uniform sampler2D Ka;
uniform sampler2D Ks;
uniform sampler2D Ns;
uniform sampler2D bump;
uniform samplerCube cubemap;
uniform Material material;
uniform Light light;
uniform int mode;
vec2 camerarange = vec2(1280, 960);
vec2 screensize = vec2(1280, 960);

float bias = 0.005;
float vis = 1;
mat3 cotangent_frame( vec3 N, vec3 p, vec2 uv ){
    // get edge vectors of the pixel triangle
    vec3 dp1 = dFdx( p );
    vec3 dp2 = dFdy( p );
    vec2 duv1 = dFdx( uv );
    vec2 duv2 = dFdy( uv );
 
    // solve the linear system
    vec3 dp2perp = cross( dp2, N );
    vec3 dp1perp = cross( N, dp1 );
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;
 
    // construct a scale-invariant frame 
    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
    return mat3( T * invmax, B * invmax, N );
}

vec3 calculatenormal( vec3 N, vec3 V, vec2 texcoord ){
    vec3 map = texture( bump, texcoord ).xyz;
    map = map * 255./127. - 128./127.;
    mat3 TBN = cotangent_frame( N, -V, texcoord );
    return normalize( TBN * map );
}


vec4 getTex(sampler2D tname){
    if(text == 1){
        vec4 col = texture(tname, textureCoord);
        float width = 0.4f;
        float edge = 0.2f;
        float dist = 1-col.a;
        float alpha = 1-smoothstep(width,width+edge,dist);
        vec3 colr = col.rgb;
        return vec4(colr, alpha);
    }
    return texture(tname, textureCoord * vec2(uvmultx, uvmulty));
}

vec4 shadify(){
    vec4 color = getTex(Kd);

    vec3 diffuse = color.rgb;

    float trans = color.a;
    
    if(trans < 0.2) discard;
    
    vec3 ambient = material.ka * diffuse;
    
    vec3 specular = vec3(1,1,1);
    
    float specpow = 1;
    if(material.hasspecpow){
        vec4 specpowvec = getTex(Ns);
        specpow = (1 - specpowvec.r);
    }else{
        specpow = material.ns;
    }

    if(material.hasspecmap){
        specular = getTex(Ks).rgb;
    }else{
        specular = material.ks;
    }
    specular = vec3(0,0,0);
    float distance = length( light.lightpos - pos.xyz );

    vec3 n = normalize(( view * model *  vec4(norm,0.0f)).xyz);
    
    if(material.hasnormmap){
       n = calculatenormal(n,eyedir,textureCoord);
    }

    vec3 l = normalize( lightdir );   
    float cosTheta = clamp( dot( n,l ), 0,1 );
    
    
    vec3 E = normalize(eyedir);
    vec3 R = reflect(-l,n);
    float cosAlpha = clamp( dot( E,R ), 0,1 );
    
    float distmult = ((distance*distance)/light.lightdistance); 
    
    vec4 fragColor = 
            vec4((ambient +
            diffuse * light.color * light.lightpower * cosTheta / distmult +
            specular * light.color * light.lightpower * pow(cosAlpha, specpow) / distmult), trans);
 
    return fragColor;
}

vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

vec4 getWaveEffect(){
    vec2 texcoord = textureCoord;
    texcoord.x += cos(texcoord.y * 4*2*3.14159) * 0.04 * time;
    return texture(Kd, clamp(vec2(texcoord.x, sin(texcoord.y)), 0.001,0.999 ));
}

float readDepth( in vec2 coord ) {
	return (2.0 * camerarange.x) / (camerarange.y + camerarange.x - texture( Ka, coord ).x * (camerarange.y - camerarange.x));	
}
 
float compareDepths( in float depth1, in float depth2 ) {
	float aoCap = 1.0;
	float aoMultiplier=100000.0;
	float depthTolerance=0.00001;
	float aorange = 100.0;// units in space the AO effect extends to (this gets divided by the camera far range
	float diff = sqrt( clamp(1.0-(depth1-depth2) / ((camerarange.y-camerarange.x)),0.0,1.0) );
	float ao = min(aoCap,max(0.0,depth1-depth2-depthTolerance) * aoMultiplier) * diff;
	return 1-ao;
}
 
vec4 ssao(){	
	float depth = readDepth( textureCoord );
	float d;
 
	float pw = 1.0 / screensize.x;
	float ph = 1.0 / screensize.y;
 
	float aoCap = 1;
 
	float ao = 0.0;
 
	float aoMultiplier=100000.0;
 
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
        fcolor2 = vec4(1-ao,1-ao,1-ao,1);
	return vec4(1-ao) * texture(Kd,textureCoord) * 2;
}

void processPP(){
    fcolor = ssao();
    //fcolor = getWaveEffect();
}

void main() {   
    if(mode == 0){
        fcolor = shadify();
    }else if(mode == 1){
        //fcolor = (getTex(texImage)/material.ka).rgb;
    }else if(mode == 2){
        fcolor = getTex(Kd);
    }else if(mode == 3){
        fcolor = getCube();
    }else if(mode == 4 || mode == 6){
        fcolor = vec4(1,1,1,1);
    }else if(mode == 5 ){
        processPP();
    }else{
        fcolor = vec4(1,1,1,1);
    }    
};
