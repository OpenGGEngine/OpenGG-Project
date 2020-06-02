@version 4.2
@include stdfrag.ggsl

uniform sampler2D Ka;

void main() {
	vec4 color1 = getTex(Kd);
	vec4 color2 = getTex(Ka);
	
	color1 = color1 + color2;
	
    fcolor = color1;
}
