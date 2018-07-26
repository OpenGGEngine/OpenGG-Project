@version 4.2
@include stdfrag.ggsl


uniform samplerCube cubemap;


vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

void main() {   
    fcolor = getCube();
}
