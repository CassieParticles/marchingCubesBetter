#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

layout(std430,binding=0) buffer outBuffer{
    float values[];
};

uniform float radius;
uniform float seed;
uniform float noiseFrequency;
uniform float noiseMagnitude;

float octave(vec3 p, float seed,float persistance);

float square(float a){
    return a*a;
}

void main() {
    int size=int(gl_NumWorkGroups.x);
    ivec3 vectorIndex=ivec3(gl_WorkGroupID);
    int scalarIndex=(vectorIndex.x*size+vectorIndex.y)*size+vectorIndex.z;
    float distFromCentre=sqrt(square(vectorIndex.x-size/2)+square(vectorIndex.y-size/2)+square(vectorIndex.z-size/2));
    float noiseVal=octave(vec3(vectorIndex)*noiseFrequency,seed,0.4);
    float val=min(((radius-distFromCentre)+noiseVal*noiseMagnitude)/radius,noiseVal+0.1);
    values[scalarIndex]=min(((radius-distFromCentre)+noiseVal*noiseMagnitude)/radius,noiseVal+0.1);
}