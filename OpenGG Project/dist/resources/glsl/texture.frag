@version 4.2
@include stdfrag.ggsl

uniform sampler2D Kd;

void main() {
    fcolor = texture(Kd, textureCoord);
    if(fcolor.a < 0.1f) discard;
}