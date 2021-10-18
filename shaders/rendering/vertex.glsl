#version 450

layout(location=0) in vec3 position;

uniform vec3 translation;

out vec3 transformedPos;

void main() {
    transformedPos=position+translation;

    gl_Position=vec4(position+translation,1);
}
