#version 450

in vec2 outTextCoords;

uniform sampler2D screenTexture;

out vec4 FragColour;

float offset=1/900.0;


void main(){
	vec2 offsets[9] = vec2[](
	vec2(-offset,  offset), // top-left
	vec2( 0.0f,    offset), // top-center
	vec2( offset,  offset), // top-right
	vec2(-offset,  0.0f),   // center-left
	vec2( 0.0f,    0.0f),   // center-center
	vec2( offset,  0.0f),   // center-right
	vec2(-offset, -offset), // bottom-left
	vec2( 0.0f,   -offset), // bottom-center
	vec2( offset, -offset)  // bottom-right
	);

	float kernel[9]=float[](
	1,1,1,
	1,-8,1,
	1,1,1
	);

	vec3 col=vec3(0);
	for(int i=0;i<9;i++){
		col+=texture(screenTexture, outTextCoords + offsets[i]).xyz*kernel[i];
	}
	col=texture(screenTexture, outTextCoords).xyz;
	FragColour=vec4(col,1);
//	FragColour=texture(screenTexture,outTextCoords);
}