#version 450

layout(location=0) in vec2 position;

uniform float scalar;

void main(){
	gl_Position=vec4(position*scalar,1,1);
}