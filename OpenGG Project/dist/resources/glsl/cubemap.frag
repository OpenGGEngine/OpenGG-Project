@version 4.2
@include stdfrag.ggsl

vec4 getCube(){
    return texture(cubemap, normalize((vec4(pos.x, -pos.y, pos.z, 1)).xyz));
}

void main() {   
    fcolor = getCube();
}
