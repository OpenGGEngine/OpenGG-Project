@version 4.2
@include stdfrag.ggsl


vec4 getCube(){
    return texture(cubemap, normalize(pos.xyz));
}

void main() {   
    fcolor = getCube();
}
