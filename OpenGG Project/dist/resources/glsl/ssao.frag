@version 4.2

@uniforms
layout(location = 0) out vec4 fcolor;

in vertexData{
    
    vec2 textureCoord;
    vec3 pos;
    vec3 norm;
};

uniform sampler2D Kd;
uniform sampler2D Ka;

@fields
vec2 camerarange = vec2(1280, 960);
vec2 screensize = vec2(1280, 960);

float bias = 0.005;
float vis = 1;

@code
vec4 getTex(sampler2D tname){
    return texture(tname, textureCoord);
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
    ao = clamp(ao, 0.0f, 0.5f);
	return vec4(1-ao);// * texture(Kd,textureCoord) * 2;
}

main() {
    fcolor = ssao();
}
