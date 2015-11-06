package mweis.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import mweis.game.entities.Particle;

public class Game implements GLEventListener {
	
	/* VM args
-Djogl.debug.DebugGL
-Djogl.debug.TraceGL
	 */
	
	public static int WIDTH, HEIGHT; // reshape will ALWAYS go off initially, which also declares these
	final int numParticles;
	enum ShaderType{ VertexShader, FragmentShader}
	
	Particle[] particles;
	int programID; // programID
	int particleVBOHandle; //
	int colorVBOHandle;
	int VAOHandle; // vao
	int VAOParticleIndex = 0; // loc of particle in vertex shader
	int VAOColorIndex = 1; // loc of color in vertex shader
	
	// timing and FPS
	long lastTimer = 0;
	long frames = 0;
	long lastFrames = 0;
	
	public Game(int numParticles){
		this.numParticles = numParticles;
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		
		GL3 gl = drawable.getGL().getGL3();
		updateScene(gl);
		renderScene(gl);
		
		// VERY inaccurate ~3-second timer
		if (System.currentTimeMillis() - lastTimer > 3000){
			lastTimer = System.currentTimeMillis();
			Launch.DEBUG(numParticles + " particles @ ~FPS: " + (frames-lastFrames)/3);
			Launch.DEBUG("FPS: " + Launch.getFPS());
			lastFrames = frames;
			if (Math.random() < .5)
				for (Particle p : particles)
					p.invertRandom();
		}
		frames++;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		Launch.DEBUG("INIT CALLED.");
		GL3 gl = drawable.getGL().getGL3();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		setup(numParticles, gl);
		preRenderOperations(gl); // call any pre-rendering ops
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		Launch.DEBUG("RESHAPE CALLED: ", x, y, width, height);
	}
	
	// does any pre-render operations
	private void preRenderOperations(GL3 gl){
		gl.glUseProgram(programID);
		gl.glBindVertexArray(VAOHandle);
	}
	
	private void renderScene(GL3 gl){
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		gl.glUseProgram(programID); // only need if we ever change program
		gl.glBindVertexArray(VAOHandle); // only need we ever bind diff VAO
		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 3*particles.length);
	}
	
	private void updateScene(GL3 gl){
		// advance all particles
		for (Particle p : particles){
			p.advance(WIDTH, HEIGHT);
		}
		// update floats
		// create big float of all particle data (for all particles) for VBO
		float[] allParticleVertexData = getAllParticleVertexData();
		
		// update VBO
		fillVBO(allParticleVertexData, particleVBOHandle, gl);
	}
	
	
	
	// for particles - setup the particles VBO, VAO, and program
	private void setup(int numParticles, GL3 gl){
		try {
			createParticles(numParticles); // make x particles
		} catch (OutOfMemoryError E){ // if x > memory, make x/2 particles (keep doing this until it fits)
			particles = null;
			setup(numParticles/2, gl);
			return;
		}
		programID = createNewProgram(gl); // program before buffers
		
		particleVBOHandle = createVBO(gl); // make a new VBO for color and points
		colorVBOHandle = createVBO(gl);
		
		// extract all vertex and color data into float[] arrays
			// create big float of all particle data (for all particles) for VBO
			float[] allParticleVertexData = getAllParticleVertexData();
			// do the same for all color data
			float[] allParticleColorData = getAllParticleColorData();
		
		

		fillVBO(allParticleVertexData, particleVBOHandle, gl);
		fillVBOStatic(allParticleColorData, colorVBOHandle, gl);
		
		// combine both VBOs together into a VAO
		VAOHandle = createVAO(gl);
		fillAndEnableVAO(VAOHandle, particleVBOHandle, colorVBOHandle, gl);
		
	}
	
	private float[] getAllParticleVertexData(){
		float[] allParticleVertexData = new float[particles.length * Particle.pointsLength];
		{
			int i=0;
			for (Particle p : particles){
				final float[] points = p.getPoints();
				for (int j=0; j < points.length; j++){
					allParticleVertexData[i++] = points[j];
				}
			}
		}
		return allParticleVertexData;
	}
	
	private float[] getAllParticleColorData() {
		float[] allParticleColorData = new float[particles.length * Particle.colorsLength];
		{
			int i=0;
			for (Particle p : particles){
				final float[] points = p.getColors();
				for (int j=0; j < points.length; j++){
					allParticleColorData[i++] = points[j];
				}
			}
		}
		return allParticleColorData;
	}
	
	private void createParticles(int numParticles){
		float low = -1f + .0001f;
		float high = 1f - .0001f;
		particles = new Particle[numParticles];
		for (int i=0; i < numParticles; i++){
			float x = (float) (Math.random() * (high - low) + low);
			float y = (float) (Math.random() * (high - low) + low);
			particles[i] = new Particle(x, y);
		}
	}
	
	// returns VBO handle for a new empty VBO
	private int createVBO(GL3 gl){
		int[] handle = new int[1];
		gl.glGenBuffers(1, handle, 0);
		return handle[0];
	}
	
	// fill a VBO w/ data
	private void fillVBO(float[] data, int handle, GL3 gl){
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, handle);
		long size = data.length * Float.SIZE / 8; // length of data * size of float / 8 (no clue why /8);
		Buffer db = Buffers.newDirectFloatBuffer(data); // buffer of data
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, size, db, GL3.GL_DYNAMIC_DRAW);
	}
	
	private void fillVBOStatic(float[] data, int handle, GL3 gl){
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, handle);
		long size = data.length * Float.SIZE / 8; // length of data * size of float / 8 (no clue why /8);
		Buffer db = Buffers.newDirectFloatBuffer(data); // buffer of data
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, size, db, GL3.GL_STATIC_DRAW);
	}
	
	private int createVAO(GL3 gl){
		int[] handle = new int[1];
		gl.glGenVertexArrays(1, handle, 0);
		return handle[0];
	}
	
	private void fillAndEnableVAO(int VAOHandle, int vertexVBO, int colorVBO, GL3 gl){
		gl.glBindVertexArray(VAOHandle);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexVBO); // bind curr VBO
		gl.glVertexAttribPointer(VAOParticleIndex, 3, GL3.GL_FLOAT, false, 0, 0); // index (bind to 0 in vertex shader), each var has 3 points each (3-dim array) (tell shader),
													// have array of floats, is normalized?, vertex stride, pointer to 1st val? default is 0
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, colorVBO);
		gl.glVertexAttribPointer(VAOColorIndex, 3, GL3.GL_FLOAT, false, 0, 0);
		
		// by default, all attributes (we just bound 2) are toggled OFF, turn these on (NOTE: THIS IS ONLY IN GL3, not GL4)
		gl.glEnableVertexAttribArray(VAOParticleIndex);
		gl.glEnableVertexAttribArray(VAOColorIndex);
	}
	
	// shader stuff (partially TAKEN FROM GL3Example - not my own code)
	
	// returns new program pointer
	/** from GL3Example */
	private int createNewProgram(GL3 gl){
		// compile shaders
		int v = this.newShaderFromCurrentClass(gl, "/vertex.shader", ShaderType.VertexShader);
		int f = this.newShaderFromCurrentClass(gl, "/fragment.shader", ShaderType.FragmentShader);
		
		// attach shaders, create, and link program
		int p = gl.glCreateProgram();
		gl.glAttachShader(p, v);
		gl.glAttachShader(p, f);
		defineProgramLayout(p, gl); // define before linking prog
		gl.glLinkProgram(p);
		
		return p;
	}
	
	// can't use (layout=x) in shader, handle this code here..
	private void defineProgramLayout(int program, GL3 gl){
		gl.glBindAttribLocation(program, VAOParticleIndex, "particle_position"); // set in "particle_position" to (layout=0)
		gl.glBindAttribLocation(program, VAOColorIndex, "particle_color"); // set in "particle_position" to (layout=1)
	}
 
	// next 3 function from jogl example - he has a good way of creating programs
	/** from tutorial */
	int newShaderFromCurrentClass(GL3 gl, String fileName, ShaderType type){
		// load the source
		String shaderSource = this.loadStringFileFromCurrentPackage(fileName);
		// define the shaper type from the enum
		int shaderType = type==ShaderType.VertexShader?GL3.GL_VERTEX_SHADER:GL3.GL_FRAGMENT_SHADER;
		// create the shader id
		int id = gl.glCreateShader(shaderType);
		//  link the id and the source
		gl.glShaderSource(id, 1, new String[] { shaderSource }, null);
		//compile the shader
		gl.glCompileShader(id);
 
		return id;
	}
	
	/** Gets a program parameter value */ /** from tutorial */
	public int getProgramParameter(GL3 gl, int obj, int paramName) {
		final int params[] = new int[1];
		gl.glGetProgramiv(obj, paramName, params, 0);
		return params[0];
	}
 
	/** from tutorial */
	protected String loadStringFileFromCurrentPackage(String fileName){
		InputStream stream = this.getClass().getResourceAsStream(fileName);
 
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		// allocate a string builder to add line per line 
		StringBuilder strBuilder = new StringBuilder();
 
		try {
			String line = reader.readLine();
			// get text from file, line per line
			while(line != null){
				strBuilder.append(line + "\n");
				line = reader.readLine();	
			}
			// close resources
			reader.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		return strBuilder.toString();
	}
}








