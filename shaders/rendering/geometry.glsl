#version 450

layout(triangles) in;
layout(triangle_strip,max_vertices=3) out;

in vec3 transformedPos[];

out vec3 fragPos;
out vec3 normal;

vec3 getNormal(){
    vec3 sideA=transformedPos[0]-transformedPos[1];
    vec3 sideB=transformedPos[2]-transformedPos[1];
    return normalize(cross(sideB,sideA));
}

void main() {
    normal=getNormal();

    gl_Position=gl_in[0].gl_Position;
    fragPos=transformedPos[0];
    EmitVertex();

    gl_Position=gl_in[1].gl_Position;
    fragPos=transformedPos[1];
    EmitVertex();

    gl_Position=gl_in[2].gl_Position;
    fragPos=transformedPos[2];
    EmitVertex();

    EndPrimitive();
}
