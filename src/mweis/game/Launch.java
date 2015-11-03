package mweis.game;

import javax.swing.JOptionPane;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
/*
opengl debug vm args
====================
-Djogl.debug.DebugGL
-Djogl.debug.TraceGL
 */
public class Launch {
	private static final boolean DEBUG = true;
	public static void DEBUG(String string){
		if(DEBUG) System.out.println("DEBUG: " + string);
	}
	public static void DEBUG(String ... strings){
		if(DEBUG){
			System.out.print("DEBUG: ");
			boolean comma = false;
			for (String string : strings){
				
				if (!comma)
					comma = true;
				else
					System.out.print(", ");
				
				System.out.print(string);
			}
			System.out.print("\n");
		}
	}
	public static void DEBUG(String prefix, int ... vars){ // debug a message + variables to display with it
		if(DEBUG){
			System.out.print("DEBUG: " + prefix + " ");
			boolean comma = false;
			for (int i : vars){
				if (!comma)
					comma = true;
				else
					System.out.print(", ");
				
				System.out.print(i);
			}
			System.out.print("\n");
		}
	}
	
	// creates a new GLWindow
	private static GLWindow newGLWindow(String name, Game game, int width, int height, boolean isFullscreen){
		System.out.println(GLProfile.glAvailabilityToString() + " --> " + GLProfile.isAnyAvailable());
		GLProfile glp = GLProfile.getMaxFixedFunc(true);
		GLCapabilitiesImmutable glCapabilities = new GLCapabilities(glp);
		GLWindow window = GLWindow.create(glCapabilities);
		window.setSize(width, height);
		
		window.setTitle(name);
		
		window.addWindowListener(
			new WindowAdapter() {
				public void windowDestroyNotify(WindowEvent event) {
			        System.exit(0);
			    }
			});
		
		window.addGLEventListener(game);
		if (isFullscreen)
			window.setFullscreen(true);
		
		return window;
	}
	
	private static Animator animator;
	public static void main(String[] args){
		// get user input for fs and numParticles:
//		final boolean isFullscreen = false;
//		final int numParticles = 500000;
        final boolean isFullscreen = 
				JOptionPane.showConfirmDialog(null, "fullscreen?", "fullscreen?", 
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
			    == JOptionPane.YES_OPTION;
		final int numParticles = Integer.parseInt(JOptionPane.showInputDialog(null, "How many particles (int)? (200,000-600,000 is good)"));
		
		Game game = new Game(numParticles);
		GLWindow frame = newGLWindow("Particles", game, 300*3, 200*3, isFullscreen); // get width/height through frame method calls
		animator = new Animator(frame); // timing mechanism
		animator.setRunAsFastAsPossible(true);
		
		frame.setVisible(true);
		animator.setRunAsFastAsPossible(true);
		animator.setUpdateFPSFrames(3, null); // sets FPS to update every 3 frames
		animator.start(); // will call init, display in game
	}
	
	public static float getFPS(){
		return animator.getLastFPS();
	}
}
