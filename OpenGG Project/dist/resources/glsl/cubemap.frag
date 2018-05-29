@version 4.2
@include stdfrag.ggsl

@fields
uniform samplerCube cubemap;

@code
vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

void main() {   
    fcolor = getCube();
}
