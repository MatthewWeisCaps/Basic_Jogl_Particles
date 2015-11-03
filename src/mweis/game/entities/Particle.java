package mweis.game.entities;

public class Particle {
	public final static int pointsLength = 9; // length of each data array
	public final static int colorsLength = 9;
	
	private final float s = 0.005f; // size
	private final float hs = s/2; // half-size
	private float points[] = new float[pointsLength];
	private float colors[] = new float[colorsLength];
	
	private float x, y, xVel, yVel;
	
	public Particle(float x, float y){
		this.x = x;
		this.y = y;
		// a particle is a triangle around (x, y)
		points[0] = x; 		points[1] = y+hs; 	points[2] = 0.0f;
		points[3] = x+hs; 	points[4] = y-hs;	points[5] = 0.0f;
		points[6] = x-hs;	points[7] = y-hs;	points[8] = 0.0f;
		
		// a particle needs colors
		for (int i=0; i < colors.length; i++){
			colors[i] = (float) Math.random(); // 0-1
		}
		
		// and velocity!
		xVel = 0.0001f; // 0 or 1
		yVel = 0.0001f;
		if (Math.random() > 0.5)
			xVel = -xVel;
		if (Math.random() > 0.5)
			yVel = -yVel;
	}
	
	// moves a step based on vels
	public void advance(int width, int height){
		float nx = x + xVel;
		float ny = y + yVel;
		x = nx;
		y = ny;
		points[0] = x; 		points[1] = y+hs; 	//points[2] = 0.0f;
		points[3] = x+hs; 	points[4] = y-hs;	//points[5] = 0.0f;
		points[6] = x-hs;	points[7] = y-hs;	//points[8] = 0.0f;
	}
	
	// invert all dirs
	public void invertRandom(){
		if (Math.random() > .5)
			xVel = -xVel;
		else
			yVel = -yVel;
	}
	
	public float[] getPoints(){
		return points;
	}
	
	public float[] getColors(){
		return colors;
	}
}
