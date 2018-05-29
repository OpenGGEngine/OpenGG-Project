@version 4.2
@include stdfrag.ggsl

@fields
uniform float exposure;
uniform float gamma;

uniform sampler2D Kd;

@code
main() {   
    vec3 color = texture(Kd, textureCoord).rgb;
  
    // Exposure tone mapping
    vec3 mapped = vec3(1.0f) - exp(-color * exposure);
    // Gamma correction
    mapped = pow(mapped, vec3(1.0f / gamma));
  
    fcolor = vec4(mapped, 1.0f);
}
