@version 4.2
@include stdfrag.ggsl


uniform sampler2D Kd;


void main() {
    fcolor = texture(Kd, textureCoord);
}