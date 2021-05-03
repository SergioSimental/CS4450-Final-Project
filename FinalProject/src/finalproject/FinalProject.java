/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;


import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

public class FinalProject 
{
    private FPCameraController fp = new FPCameraController(0f, 0f, 0f);
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    private FloatBuffer ambientLightModel;
    
    // method: start
    // purpose: the start creates the window, initializes the GL context
    // and starts the render logic
    
    public void start()
    {
        try
        {
            createWindow();
            initGL();
            fp.gameLoop();//render();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    private void initLightArrays() 
    {
        lightPosition= BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.f).put(0.0f).put(1.0f).flip();
        whiteLight= BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
        ambientLightModel = BufferUtils.createFloatBuffer(4);
        ambientLightModel.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
    
    // method: createWindow
    // purpose: this method draws the window the for the game to run
    // this function will throw an error if for some reason the gl doesn't work
    private void createWindow() throws Exception
    {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i = 0; i < d.length; i++){
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32){
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Minecraft Clone");
        Display.create();
    }
    
    // method: initGL
    // purpose: to setup the opengl options
    private void initGL() 
    {
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (
                float)displayMode.getWidth()/(float)displayMode.getHeight(),
                0.1f, 
                300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    // method: main
    // purpose: the ever important main method that runs first
    // it creates a new object and starts the graphics system
    public static void main(String[] args)
    {
        FinalProject finalProject= new FinalProject();
        finalProject.start();
    }
}
