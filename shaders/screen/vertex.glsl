#version 450

layout(location=0) vec3 vertexIn;

void main() {
    gl_Position=vec4(vertexIn,1);
}
