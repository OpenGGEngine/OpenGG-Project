@version 4.5
@include stdfrag.ggsl

void main() {
    fcolor = texture(Kd, textureCoord);
    if(fcolor.a < 0.1f) discard;
}