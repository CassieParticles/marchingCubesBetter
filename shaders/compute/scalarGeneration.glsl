#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

layout(std430,binding=0) buffer outBuffer{
    float values[];
};

uniform float radius;

float square(float a){
    return a*a;
}

void main() {
    int size=int(gl_NumWorkGroups.x);
    ivec3 vectorIndex=ivec3(gl_WorkGroupID);
    int scalarIndex=(vectorIndex.x*size+vectorIndex.y)*size+vectorIndex.z;
    float distFromCentre=square(vectorIndex.x-size/2)+square(vectorIndex.y-size/2)+square(vectorIndex.z-size/2);
    values[scalarIndex]=(square(radius)-distFromCentre)/square(radius);
}
