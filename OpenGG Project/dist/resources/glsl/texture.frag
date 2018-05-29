@version 4.2
@include stdfrag.ggsl

@fields
uniform sampler2D Kd;

@code
void main() {   
    fcolor = texture(Kd, textureCoord);
}