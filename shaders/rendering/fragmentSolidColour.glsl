#version 450

in vec3 fragPos;
in vec3 normal;

uniform vec3 colour;
uniform vec3 cameraPos;

out vec4 FragColour;

float calcLighting(){
    vec3 dirToCam=normalize(cameraPos-fragPos);
    float diffuseLighting=dot(dirToCam,normal);
    return clamp(max(0.2,diffuseLighting),0,1);
}

void main() {
    vec3 colour=(normal-vec3(1,1,1))/-2;
    FragColour=vec4(calcLighting()*colour,1);
}
