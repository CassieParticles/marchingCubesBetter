#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

int getEdges(int index);
int[16] getTriangle(int index); //Defining methods to access information from triangulation.glsl

uniform int size;


int getIndexFromInvoc(ivec3 vec){
    return (vec.x*size+vec.y)*size+vec.z;
}

int square(int f){
    return f*f;
}

float normalise(float a, float b, float r){
    if(a==b){
        return 0;
    }
    return(r-a)/(b-a);
}

layout(location=0,r32f) uniform image3D scalarTexture;

layout(std430, binding=0) buffer vertexBuffer{
    vec3[] vertices;
};

void main() {
    ivec3 globalInvoc=ivec3(gl_WorkGroupID);
    int globalIndex=getIndexFromInvoc(globalInvoc);
    float corners[8]={
        imageLoad(scalarTexture,globalInvoc+ivec3(0,0,0)),
        imageLoad(scalarTexture,globalInvoc+ivec3(1,0,0)),
        imageLoad(scalarTexture,globalInvoc+ivec3(1,0,1)),
        imageLoad(scalarTexture,globalInvoc+ivec3(0,0,1)),
        imageLoad(scalarTexture,globalInvoc+ivec3(0,1,0)),
        imageLoad(scalarTexture,globalInvoc+ivec3(1,1,0)),
        imageLoad(scalarTexture,globalInvoc+ivec3(1,1,1)),
        imageLoad(scalarTexture,globalInvoc+ivec3(0,1,1))};

    int voxelIndex=0;

    if(corners[0]>=threshold){voxelIndex+=1;}
    if(corners[1]>=threshold){voxelIndex+=2;}
    if(corners[2]>=threshold){voxelIndex+=4;}
    if(corners[3]>=threshold){voxelIndex+=8;}
    if(corners[4]>=threshold){voxelIndex+=16;}
    if(corners[5]>=threshold){voxelIndex+=32;}
    if(corners[6]>=threshold){voxelIndex+=64;}
    if(corners[7]>=threshold){voxelIndex+=128;}

    int edges=getEdges(voxelIndex);
    int indices[16]=getTriangle(voxelIndex);

}
