#version 410 core

layout(triangles_adjacency) in;
layout(triangle_strip, max_vertices=18) out;

in gl_PerVertex
{
  vec4 gl_Position;
  float gl_PointSize;
  float gl_ClipDistance[];
} gl_in[];

out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
    
    vec2 textureCoord;
    vec4 pos;
    vec3 norm;
};

in vec2 textureCoords[];
in vec3 poss[];
in vec3 norms[];

struct Light
{
    float lightdistance;
    float lightpower;
    vec3 lightpos;
    vec3 color;
};

uniform int mode;
uniform Light light;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

float epsilon = 0.0009;

void EmitQuad(vec3 StartVertex, vec3 EndVertex)
 {
	mat4 mvp = projection * view;
	
     // Vertex #1: the starting vertex (just a tiny bit below the original edge)
     vec3 lightdir = normalize(StartVertex - light.lightpos); 
     gl_Position = mvp * vec4((StartVertex + lightdir * epsilon), 1.0f);
     EmitVertex();

     // Vertex #2: the starting vertex projected to infinity
     gl_Position = mvp * vec4(lightdir, 0.0);
     EmitVertex();

     // Vertex #3: the ending vertex (just a tiny bit below the original edge)
     lightdir = normalize(EndVertex - light.lightpos);
     gl_Position = mvp * vec4((EndVertex + lightdir * epsilon), 1.0f);
     EmitVertex();

     // Vertex #4: the ending vertex projected to infinity
     gl_Position = mvp * vec4(lightdir , 0.0);
     EmitVertex();

     EndPrimitive(); 
 };


 void main()
 {
     vec3 e1 = poss[2] - poss[0];
     vec3 e2 = poss[4] - poss[0];
     vec3 e3 = poss[1] - poss[0];
     vec3 e4 = poss[3] - poss[2];
     vec3 e5 = poss[4] - poss[2];
     vec3 e6 = poss[5] - poss[0];
	
     vec3 Normal = cross(e1,e2);
     vec3 ldir = light.lightpos - poss[0];
	 
	 mat4 mvp = projection * view;

     // Handle only light facing triangles
     if (dot(Normal, ldir) > 0) {

         Normal = cross(e3,e1);

         if (dot(Normal, ldir) <= 0) {
             vec3 StartVertex = poss[0];
             vec3 EndVertex = poss[2];
             EmitQuad(StartVertex, EndVertex);
         }

         Normal = cross(e4,e5);
         ldir = light.lightpos - poss[2];

         if (dot(Normal, ldir) <= 0) {
             vec3 StartVertex = poss[2];
             vec3 EndVertex = poss[4];
             EmitQuad(StartVertex, EndVertex);
         }

         Normal = cross(e2,e6);
         ldir = light.lightpos - poss[4];

         if (dot(Normal, ldir) <= 0) {
             vec3 StartVertex = poss[4];
             vec3 EndVertex = poss[0];
             EmitQuad(StartVertex, EndVertex);
         }

         // render the front cap
         ldir = (normalize(poss[0] - light.lightpos));
         gl_Position = mvp * vec4((poss[0] + ldir * epsilon), 1.0f);
         EmitVertex();

         ldir = (normalize(poss[2] - light.lightpos));
         gl_Position = mvp * vec4((poss[2] + ldir * epsilon), 1.0f);
         EmitVertex();

         ldir = (normalize(poss[4] - light.lightpos));
         gl_Position = mvp * vec4((poss[4] + ldir * epsilon), 1.0f);
         EmitVertex();
         EndPrimitive();

         // render the back cap
         ldir = poss[0] - light.lightpos;
         gl_Position = mvp * vec4(ldir, 0.0);
         EmitVertex();

         ldir = poss[4] - light.lightpos;
         gl_Position = mvp * vec4(ldir, 0.0);
         EmitVertex();

         ldir = poss[2] - light.lightpos;
         gl_Position = mvp * vec4(ldir, 0.0);
         EmitVertex();
     }
 }
