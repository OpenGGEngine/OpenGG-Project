@version 4.2
@include stdfrag.ggsl


layout(location = 1) out vec4 bright;

uniform sampler2D Kd;


void main() {
    vec3 val = getTex(Kd).xyz;
    bright = vec4(0,0,0,1);
    fcolor = vec4(val,1);
	if(dot(val.rgb, vec3(0.2126, 0.7152, 0.0722)) > 0.9f)
    		bright = vec4(val,1);
}
