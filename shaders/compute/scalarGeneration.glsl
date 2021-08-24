#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1)in;

layout(location=0,r32f) uniform image3D scalarTexture;

uniform int size;

int square(int s){
    return s*s;
}

void main() {
    ivec3 globalInvoc=ivec3(gl_WorkGroupID);
    ivec3 centredPos=globalInvoc-ivec3(size/2);
    float val=square(centredPos.x)+square(centredPos.y)+square(centredPos.z);
    imageStore(scalarTexture,globalInvoc,vec4(val,0,0,0));
}
