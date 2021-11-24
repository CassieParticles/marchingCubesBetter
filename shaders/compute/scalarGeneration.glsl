#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

layout(std430,binding=0) buffer outBuffer{
    float values[];
};

uniform float radius;
uniform float seed;
uniform float noiseFrequency;
uniform float noiseMagnitude;

float rand(vec3 co, float seed);
float octave(vec3 p, float seed,float persistance);

float square(float a){
    return a*a;
}

void main() {
    int size=int(gl_NumWorkGroups.x);
    ivec3 vectorIndex=ivec3(gl_WorkGroupID);
    int scalarIndex=(vectorIndex.x*size+vectorIndex.y)*size+vectorIndex.z;
    float distFromCentre=sqrt(square(vectorIndex.x-size/2)+square(vectorIndex.y-size/2)+square(vectorIndex.z-size/2));
    values[scalarIndex]=((radius-distFromCentre)+octave(vec3(vectorIndex)*noiseFrequency,seed,0.6)*noiseMagnitude)/radius;
//    values[scalarIndex]=octave(vec3(vectorIndex)*noiseFrequency,seed,0.8);
}