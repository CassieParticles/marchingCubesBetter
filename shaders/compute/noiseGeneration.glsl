#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

float rand(vec3 co, float seed)
{
    return fract(sin(dot(co.xyz ,vec3(12.9898,78.233,45.27361)*seed)) * 43758.5453*seed);
}

float smoothStep(float t){
    return t*t*t*(t*(t*6-15)+10);
}

float interpolate(float a, float b, float r){
    return (b-a)*smoothStep(r)+a;
}

float noise(vec3 p, float seed){
    vec3 floorP=floor(p);
    vec3 fractP=fract(p);

    float gFloat[8]=float[8](
        rand(floorP+vec3(0,0,0),seed),
        rand(floorP+vec3(0,0,1),seed),
        rand(floorP+vec3(0,1,0),seed),
        rand(floorP+vec3(0,1,1),seed),
        rand(floorP+vec3(1,0,0),seed),
        rand(floorP+vec3(1,0,1),seed),
        rand(floorP+vec3(1,1,0),seed),
        rand(floorP+vec3(1,1,1),seed)
    );

    float z1=interpolate(gFloat[2],gFloat[3],fractP.z);
    float z0=interpolate(gFloat[0],gFloat[1],fractP.z);
    float z2=interpolate(gFloat[4],gFloat[5],fractP.z);
    float z3=interpolate(gFloat[6],gFloat[7],fractP.z);

    float y0=interpolate(z0,z1,fractP.y);
    float y1=interpolate(z2,z3,fractP.y);

    return interpolate(y0,y1,fractP.x)*2-1;
}

float octave(vec3 p, float seed, float persistance){
    float total=0;
    float frequency=1;
    float magnitude=1;
    float max=0;
    for(int i=0;i<8;i++){
        total+=noise(p*frequency,seed)*magnitude;
        max+=magnitude;

        frequency*=2;
        magnitude*=persistance;
    }
    return total/max;
}