@version 4.5
@include phong.ggsl
@include stdfrag.ggsl

uniform float cuboidscaling;
uniform vec3 cuboidscale;

void main() {
    vec2 newCoord = textureCoord;
    if(cuboidscaling != 0f){
        vec3 untranformedNormal = normalize(vec3(inverse(model) * vec4(norm,0.0)));
        if(untranformedNormal.x != 0f){
            newCoord = vec2(cuboidscale.y,cuboidscale.z)*textureCoord;
        }else if(untranformedNormal.y != 0f){
            newCoord = vec2(cuboidscale.x,cuboidscale.z)*textureCoord;
        }else if(untranformedNormal.z != 0f){
            newCoord = vec2(cuboidscale.x,cuboidscale.y)*textureCoord;
        }
    }

    generatePhongData();
    Material material;

    material.hasnormmap = 0.0f;
    material.hasambmap = 0.0f;
    material.hasspec = 0.0f;
    material.hasspecpow = 0.0f;
    material.hascolormap = 1.0f;
    material.hasem = 0.0f;
    material.ks = vec3(0,0,0);
    material.ka = vec3(0,0,0);
    material.kd = vec3(0,0,0);
    material.ns = 128;

    useMaterial(material, texture(Kd, newCoord));

    vec3 col = ambient;
    col += emmisive;

    for(int i = 0; i < numLights; i++){
        col += getPhongFrom(lights[i]);
    }
    fcolor = vec4(col, trans);
}
