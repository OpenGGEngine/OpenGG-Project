@version 4.2
@include stdfrag.ggsl
uniform sampler2D Kd;
uniform sampler2D Ka;


vec2 camerarange = vec2(1280, 960);
vec2 screensize = vec2(1280, 960);

float bias = 0.005;
float vis = 1;


float readDepth(vec2 coord ) {
	return (2.0 * camerarange.x) / (camerarange.y + camerarange.x - texture( Ka, coord ).x * (camerarange.y - camerarange.x));	
}
 
float compareDepths(float depth1, float depth2 ) {
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
 
	pw *= 2.0f;
	ph *= 2.0f;
	aoMultiplier /= 2.0f;
	aoscale *= 1.2f;
 
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
    ao = clamp(ao, 0.5f, 1.0f);
	return vec4(ao,ao,ao,1) * texture(Kd,textureCoord);
}

void main() {
    fcolor = ssao();
}
