#version 450

in vec2 outTextCoords;

uniform sampler2D screenTexture;

layout(location=0) out vec4 FragColour;

void main(){
	FragColour=texture(screenTexture,outTextCoords);
}