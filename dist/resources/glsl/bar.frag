@version 4.5
@include stdfrag.ggsl
uniform vec3 fill;
uniform vec3 back;
uniform float percent;

void main() {
	if(textureCoord.x < percent){
		fcolor = vec4(fill,1);
	}else{
		fcolor = vec4(back,1);
	}
}
