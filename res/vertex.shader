#version 150

//layout(location=0) in vec3 particle_position;
//layout(location=1) in vec3 particle_color;
// layout code isn't universal - it's defined in Game.java under the "defineProgramLayout" function
in vec3 particle_position;
in vec3 particle_color;

out vec3 color;

void main() // main(void)?
{    
//    color = vec3(particle_color.r  * particle_position.x, particle_color.g, particle_color.b);
// compute color
    color = particle_color * abs(particle_position);
    if ( max(color.x, max(color.y, color.z)) < 0.01){
    	color = particle_color;
    }
	
// make brightness follow a sin curve
	// make all position coords relative to -pi to pi
//	float sina = particle_position.x * 3.14159265359; // -1.0 to 1.0 * pi
//	float sinY = sin(sina); // -1 to 1
	// get distance between where sin line is and where this particle is
//	float dist = abs(sinY-particle_position.y);//distance(sinY, particle_position.y); // dist between sinY (-1 to 1) and particle_position.y (-1 to 1)
//	float iDist = 1-dist; // inverse of distance
	
//	color = color * iDist; // make all color's brightness a factor of distance from sin curve
	
    gl_Position = vec4(particle_position, 1.0);
}