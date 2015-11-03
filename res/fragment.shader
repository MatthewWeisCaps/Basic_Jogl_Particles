#version 150

in vec3 color;
out vec4 frag_color; // default of frag shader is one vec4 color output. that's why this works. Bound to 0 in code.

void main(void)
{    
   frag_color = vec4(color, 1.0);
}