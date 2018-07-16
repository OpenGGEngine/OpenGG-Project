COMPILED GGSL ERROR SOURCE: From shader fxaa.frag with error code 0: ERROR: 0:13: 'f' : syntax error syntax error
#version 430 core
layout(location = 0) out vec4 fcolor;
 in vertexData{
 vec2 textureCoord;
 vec3 pos;
 vec3 norm;
};
 uniform mat4 view;
 uniform mat4 model;
 uniform mat4 perspective;
 const vec2 u_texelStep =  vec2(0.02f, 0.02f);
 const int u_showEdges =  0;
 const float u_lumaThreshold =  1/8f;
 const float u_mulReduce =  0.1f;
 const float u_minReduce =  0.1f;
 const float u_maxSpan =  0.1f;
 uniform sampler2D Kd;
vec4 getTex(sampler2D tname){
	return texture(tname, textureCoord);
}
void main(){
	vec3 rgbM = texture(Kd, textureCoord).rgb;
			vec3 rgbNW = textureOffset(Kd, textureCoord, ivec2(-1, 1)).rgb;
 vec3 rgbNE = textureOffset(Kd, textureCoord, ivec2(1, 1)).rgb;
 vec3 rgbSW = textureOffset(Kd, textureCoord, ivec2(-1, -1)).rgb;
 vec3 rgbSE = textureOffset(Kd, textureCoord, ivec2(1, -1)).rgb;
		const vec3 toLuma = vec3(0.299, 0.587, 0.114);
	
		float lumaNW = dot(rgbNW, toLuma);
	float lumaNE = dot(rgbNE, toLuma);
	float lumaSW = dot(rgbSW, toLuma);
	float lumaSE = dot(rgbSE, toLuma);
	float lumaM = dot(rgbM, toLuma);
		float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	
		if (lumaMax - lumaMin < lumaMax * u_lumaThreshold)
	{
				fcolor = vec4(rgbM, 1.0);
		
		return;
	} 
	
		vec2 samplingDirection;	
	samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
 samplingDirection.y = ((lumaNW + lumaSW) - (lumaNE + lumaSE));
 
   float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * u_mulReduce, u_minReduce);
		float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);
 
  samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-u_maxSpan, -u_maxSpan), vec2(u_maxSpan, u_maxSpan)) * u_texelStep;
	
		vec3 rgbSampleNeg = texture(Kd, textureCoord + samplingDirection * (1.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePos = texture(Kd, textureCoord + samplingDirection * (2.0/3.0 - 0.5)).rgb;
	vec3 rgbTwoTab = (rgbSamplePos + rgbSampleNeg) * 0.5; 
		vec3 rgbSampleNegOuter = texture(Kd, textureCoord + samplingDirection * (0.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePosOuter = texture(Kd, textureCoord + samplingDirection * (3.0/3.0 - 0.5)).rgb;
	
	vec3 rgbFourTab = (rgbSamplePosOuter + rgbSampleNegOuter) * 0.25 + rgbTwoTab * 0.5; 
	
		float lumaFourTab = dot(rgbFourTab, toLuma);
	
		if (lumaFourTab < lumaMin || lumaFourTab > lumaMax)
	{
				fcolor = vec4(rgbTwoTab, 1.0); 
	}
	else
	{
				fcolor = vec4(rgbFourTab, 1.0);
	}
		if (u_showEdges != 0)
	{
		fcolor.r = 1.0;
	}
}
