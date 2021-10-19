 #version 450

layout(triangles) in;
layout(triangle_strip,max_vertices=3) out;

in vec3 transformedPos[];

 uniform mat4 projectionMatrix;
 uniform mat4 viewMatrix;
 uniform vec3 translation;
 uniform float time;
 uniform int explodeTrue;

out vec3 fragPos;
out vec3 normal;

vec3 getNormal(){
    vec3 sideA=transformedPos[0]-transformedPos[1];
    vec3 sideB=transformedPos[2]-transformedPos[1];
    return normalize(cross(sideB,sideA));
}



 vec4 explode(vec4 position, vec3 normal){
     float magnitude = 15.0;
     vec3 direction = normal * (1-cos(time)) * magnitude;
     return position + vec4(direction*explodeTrue, 0.0);
 }

 vec3 explode(vec3 position, vec3 normal){
     float magnitude = 15.0;
     vec3 direction = normal * pow(2,time)-2 * magnitude;
     return position + direction;
 }


void main() {
    normal=getNormal();

    gl_Position=projectionMatrix*viewMatrix*explode(gl_in[0].gl_Position,normal);
    fragPos=transformedPos[0];
    EmitVertex();

    gl_Position=projectionMatrix*viewMatrix*explode(gl_in[1].gl_Position,normal);
    fragPos=transformedPos[1];
    EmitVertex();

    gl_Position=projectionMatrix*viewMatrix*explode(gl_in[2].gl_Position,normal);
    fragPos=transformedPos[2];
    EmitVertex();

    EndPrimitive();
}
