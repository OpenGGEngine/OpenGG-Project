@version 4.5

layout(location = 0) out vec4 fcolor;

layout(location = 5) in vec4 FragPos;

uniform sampler2D Kd;
uniform vec3 lightPos;
uniform float farplane;

void main()
{
    float lightDistance = length(FragPos.xyz - lightPos);

    lightDistance = lightDistance / farplane;

    gl_FragDepth = lightDistance;
    fcolor = vec4(lightDistance,lightDistance,lightDistance,1);

}
