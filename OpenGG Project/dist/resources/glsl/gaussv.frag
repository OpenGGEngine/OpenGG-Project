@version 4.2
@include stdfrag.ggsl

uniform sampler2D Kd;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);


void main()
{
    vec2 tex_offset = 1.0 / textureSize(Kd, 0); // gets size of single texel
    vec3 result = texture(Kd, textureCoord).rgb * weight[0]; // current fragment's contribution

    for(int i = 1; i < 5; i++)
    {
        result += texture(Kd, textureCoord + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        result += texture(Kd, textureCoord - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
    }

    fcolor = vec4(result, 1.0);
}