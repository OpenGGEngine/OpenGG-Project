@version 4.2

layout(location = 0) out vec4 fcolor;

in vec4 FragPos;

uniform vec3 lightPos;
uniform float farplane;

void main()
{
    float lightDistance = length(FragPos.xyz - lightPos);

    lightDistance = lightDistance / farplane;

    gl_FragDepth = lightDistance;
    fcolor = vec4(lightDistance,lightDistance,lightDistance,1);

}
