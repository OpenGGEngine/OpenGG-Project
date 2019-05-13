@version 4.2
@include stdfrag.ggsl

vec4 getCube(){
    return texture(cubemap, normalize((model * vec4(pos.xyz,1)).xyz));
}

void main() {   
    fcolor = getCube();
}
