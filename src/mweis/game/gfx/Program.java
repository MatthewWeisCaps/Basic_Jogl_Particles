//package mweis.game.gfx;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//
//import javax.media.opengl.GL3;
//
//import mweis.game.Launch;
//
//public class Program {
//	// convention for this class: pointers are prefixed with an underscore
//	private static enum ShaderType{ VertexShader, FragmentShader}
//	
//	private final int POINTER;
//	private final Shader vertex, fragment; // the two shaders
//	
//	private class Shader {
//		public final ShaderType type;
//		private final HashMap<String, Integer> ins;
//		private final HashMap<String, Integer> uniforms;
//		
//		private Shader(String[] ins, String[] uniforms, ShaderType type){
//			this.type = type;
//			this.ins = new HashMap<String, Integer>();
//			this.uniforms = new HashMap<String, Integer>();
//			
//			// create the ins HashMap:
//			for (String in : ins){
//				// get and store id
//			}
//			
//			// create the ins HashMap:
//			for (String uniform : uniforms){
//				// get and store id
//			}
//		}
//		
//		private Shader(String[] ins, ShaderType type){
//			this.type = type;
//			this.ins = new HashMap<String, Integer>();
//			this.uniforms = null;
//			
//			// create the ins HashMap:
//			for (String in : ins){
//				// get and store id
//			}
//		}
//		
//		
//		public int getInPointer(String name){
//			try {
//				return ins.get(name);
//			} catch (Exception e){
//				Launch.DEBUG("error getting in pointer from Program.java - " + e.getMessage());
//			}
//			return -1;
//		}
//		
//		public int getUniformPointer(String name){
//			try {
//				return uniforms.get(name);
//			} catch (Exception e){
//				Launch.DEBUG("error getting uniform pointer from Program.java - " + e.getMessage());
//			}
//			return -1;
//		}
//	}
//	
//	
//	
//	
//	public Program(String vertex, String fragment){
//		POINTER = 0;
//		this.vertex = new Shader();
//		this.fragment = null;
//		
//		
//	}
//	
//	
//	
//	/** Retrieves the info log for the shader */
//	public String getShaderInfoLog(GL3 gl, int obj) {
//		// Otherwise, we'll get the GL info log
//		final int logLen = getShaderParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
//		if (logLen <= 0)
//			return "";
// 
//		// Get the log
//		final int[] retLength = new int[1];
//		final byte[] bytes = new byte[logLen + 1];
//		gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
//		final String logMessage = new String(bytes);
// 
//		return String.format("ShaderLog: %s", logMessage);
//	}
// 
//	/** Retrieves the info log for the program */
//	public String printProgramInfoLog(GL3 gl, int obj) {
//		// get the GL info log
//		final int logLen = getProgramParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
//		if (logLen <= 0)
//			return "";
// 
//		// Get the log
//		final int[] retLength = new int[1];
//		final byte[] bytes = new byte[logLen + 1];
//		gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
//		final String logMessage = new String(bytes);
// 
//		return logMessage;
//	}
// 
//	/** Get a shader parameter value. See 'glGetShaderiv' */
//	private int getShaderParameter(GL3 gl, int obj, int paramName) {
//		final int params[] = new int[1];
//		gl.glGetShaderiv(obj, paramName, params, 0);
//		return params[0];
//	}
//	
//	/** Gets a program parameter value */
//	public int getProgramParameter(GL3 gl, int obj, int paramName) {
//		final int params[] = new int[1];
//		gl.glGetProgramiv(obj, paramName, params, 0);
//		return params[0];
//	}
// 
//	protected String loadStringFileFromCurrentPackage( String fileName){
//		InputStream stream = this.getClass().getResourceAsStream(fileName);
// 
//		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//		// allocate a string builder to add line per line 
//		StringBuilder strBuilder = new StringBuilder();
// 
//		try {
//			String line = reader.readLine();
//			// get text from file, line per line
//			while(line != null){
//				strBuilder.append(line + "\n");
//				line = reader.readLine();	
//			}
//			// close resources
//			reader.close();
//			stream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
// 
//		return strBuilder.toString();
//	}
// 
//	int newProgram(GL3 gl) {
//		// create the two shader and compile them
//		int v = this.newShaderFromCurrentClass(gl, "/vertex.shader", ShaderType.VertexShader);
//		int f = this.newShaderFromCurrentClass(gl, "/fragment.shader", ShaderType.FragmentShader);
// 
//		System.out.println(getShaderInfoLog(gl, v));
//		System.out.println(getShaderInfoLog(gl, f));
// 
//		int p = this.createProgram(gl, v, f);
// 
//		//http://www.lighthouse3d.com/tutorials/glsl-core-tutorial/fragment-shader/
//		gl.glBindFragDataLocation(p, 0, "outColor"); // this call binds the output "named outcolor" to the location 0.
//		printProgramInfoLog(gl, p);
// 
////		this.vertexLoc = gl.glGetAttribLocation( p, "position");
////		this.colorLoc = gl.glGetAttribLocation( p, "color");
//// 
////		this.projMatrixLoc = gl.glGetUniformLocation( p, "projMatrix");
////		this.viewMatrixLoc = gl.glGetUniformLocation( p, "viewMatrix");
// 
//		return p;
//	}
// 
//	private int createProgram(GL3 gl, int vertexShaderId, int fragmentShaderId) {
//		// generate the id of the program
//		int programId = gl.glCreateProgram();
//		// attach the two shader
//		gl.glAttachShader(programId, vertexShaderId);
//		gl.glAttachShader(programId, fragmentShaderId);
//		// link them
//		gl.glLinkProgram(programId);
// 
//		return programId;
//	}
// 
//	int newShaderFromCurrentClass(GL3 gl, String fileName, ShaderType type){
//		// load the source
//		String shaderSource = this.loadStringFileFromCurrentPackage(fileName);
//		// define the shaper type from the enum
//		int shaderType = type==ShaderType.VertexShader?GL3.GL_VERTEX_SHADER:GL3.GL_FRAGMENT_SHADER;
//		// create the shader id
//		int id = gl.glCreateShader(shaderType);
//		//  link the id and the source
//		gl.glShaderSource(id, 1, new String[] { shaderSource }, null);
//		//compile the shader
//		gl.glCompileShader(id);
// 
//		return id;
//	}
//	
//}
