@version 4.5
@include stdfrag.ggsl

uniform vec3 color;

void main() {
    float alpha = texture(Kd, textureCoord).a;
    if(alpha < 0.1f) discard;
    fcolor = vec4(color, alpha);
}