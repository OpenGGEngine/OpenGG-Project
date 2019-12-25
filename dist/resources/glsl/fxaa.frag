@version 4.3
@include stdfrag.ggsl

const vec2 u_texelStep = vec2(0.02f, 0.02f);
const int u_showEdges = 0;

const float u_lumaThreshold = 1.0f/4.0f;
const float u_mulReduce = 0.01f;
const float u_minReduce = 0.01f;
const float u_maxSpan = 0.04f;

uniform sampler2D Kd;


// see FXAA
// http://developer.download.nvidia.com/assets/gamedev/files/sdk/11/FXAA_WhitePaper.pdf
// http://iryoku.com/aacourse/downloads/09-FXAA-3.11-in-15-Slides.pdf
// http://horde3d.org/wiki/index.php5?title=Shading_Technique_-_FXAA

void main() {
    vec3 rgbM = texture(Kd, textureCoord).rgb;

	// Possibility to toggle FXAA on and off.

	// Sampling neighbour texels. Offsets are adapted to OpenGL texture coordinates. 
	vec3 rgbNW = textureOffset(Kd, textureCoord, ivec2(-1, 1)).rgb;
    vec3 rgbNE = textureOffset(Kd, textureCoord, ivec2(1, 1)).rgb;
    vec3 rgbSW = textureOffset(Kd, textureCoord, ivec2(-1, -1)).rgb;
    vec3 rgbSE = textureOffset(Kd, textureCoord, ivec2(1, -1)).rgb;

	// see http://en.wikipedia.org/wiki/Grayscale
	const vec3 toLuma = vec3(0.299, 0.587, 0.114);
	
	// Convert from RGB to luma.
	float lumaNW = dot(rgbNW, toLuma);
	float lumaNE = dot(rgbNE, toLuma);
	float lumaSW = dot(rgbSW, toLuma);
	float lumaSE = dot(rgbSE, toLuma);
	float lumaM = dot(rgbM, toLuma);

	// Gather minimum and maximum luma.
	float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	
	// If contrast is lower than a maximum threshold ...
	if (lumaMax - lumaMin < lumaMax * u_lumaThreshold)
	{
		// ... do no AA and return.
		fcolor = vec4(rgbM, 1.0);
		
		return;
	}  
	
	// Sampling is done along the gradient.
	vec2 samplingDirection;	
	samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    samplingDirection.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
    
    // Sampling step distance depends on the luma: The brighter the sampled texels, the smaller the final sampling step direction.
    // This results, that brighter areas are less blurred/more sharper than dark areas.  
    float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * u_mulReduce, u_minReduce);

	// Factor for norming the sampling direction plus adding the brightness influence. 
	float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);
    
    // Calculate final sampling direction vector by reducing, clamping to a range and finally adapting to the texture size. 
    samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-u_maxSpan, -u_maxSpan), vec2(u_maxSpan, u_maxSpan)) * u_texelStep;
	
	// Inner samples on the tab.
	vec3 rgbSampleNeg = texture(Kd, textureCoord + samplingDirection * (1.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePos = texture(Kd, textureCoord + samplingDirection * (2.0/3.0 - 0.5)).rgb;

	vec3 rgbTwoTab = (rgbSamplePos + rgbSampleNeg) * 0.5;  

	// Outer samples on the tab.
	vec3 rgbSampleNegOuter = texture(Kd, textureCoord + samplingDirection * (0.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePosOuter = texture(Kd, textureCoord + samplingDirection * (3.0/3.0 - 0.5)).rgb;
	
	vec3 rgbFourTab = (rgbSamplePosOuter + rgbSampleNegOuter) * 0.25 + rgbTwoTab * 0.5;   
	
	// Calculate luma for checking against the minimum and maximum value.
	float lumaFourTab = dot(rgbFourTab, toLuma);
	
	// Are outer samples of the tab beyond the edge ... 
	if (lumaFourTab < lumaMin || lumaFourTab > lumaMax)
	{
		// ... yes, so use only two samples.
		fcolor = vec4(rgbTwoTab, 1.0); 
	}
	else
	{
		// ... no, so use four samples. 
		fcolor = vec4(rgbFourTab, 1.0);
	}

	// Show edges for debug purposes.	
	if (u_showEdges != 0)
	{
		fcolor.r = 1.0;
	}
}