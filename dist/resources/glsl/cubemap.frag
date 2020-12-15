@version 4.5
@include stdfrag.ggsl

vec4 getCube(){
    return texture(cubemap, vec3(pos.x, -pos.y, pos.z));
}

void main() {   
    fcolor = getCube();
}
