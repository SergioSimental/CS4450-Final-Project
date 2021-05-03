/***************************************************************
* file: FPCameraController.java
* authors: Val Feist, Eric Platt, Sergio Simental
* class: CS 4450 Computer Graphics
*
* assignment: Final Project Final Checkpoint
* date last modified: 4/23/2020
*
* purpose: FPCameraController allows the player to controller the 
* camera of the scene using the keyboard and mouse, creates the
* the cube shown in the scene, and maintains the game running 
*
****************************************************************/

package finalproject;


import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;

public class FPCameraController {
    //3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lPosition = null;
    
    //booleans to keep track of light source's intended movement for Day/Night cycle
    private boolean goingUp = false;
    private boolean goingRight = false;
    
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;
    private Vector3Float me;
    
    //time that a jump is supposed to last
    private int jumptime;
    
    // following members were added for checkpoint two
    // our chunks to render
    private Chunks chunk;
    
    // a simple member to create a chunk on the first render loop
    // perhaps a better solution should be considered
    private boolean firstRendIteration = true;
    
    //method: FPCameraController
    //purpose: Constructor of FPCameraController sets the position
    // of the camera 
    public FPCameraController(float x, float y, float z){
        //instantiate position Vector3f to the x y z parameters
        position = new Vector3f(x,y,z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 100f;//at this value, x is between x=-20 and x=220. This ensures a circular path above and under the map for the cycle.
        lPosition.y = 220f;//at this value, y is at the extreme of y=220. This ensures a circular path above and under the map for the cycle.
        lPosition.z = 100f;
        jumptime = 3000;
        position.y -= 50;
        position.x -= 40;
        position.z -= 40;
    }
    
    //method: yaw
    //purpose: increment the camera's current yaw rotation
    public void yaw(float amount){
        //increment the yaw by the amount parameter
        yaw += amount;
    }
    
    //method: pitch
    //purpose: increment the camera's current yaw rotation
    public void pitch(float amount){
        //increment the pitch by the amount parameter
        pitch -= amount;
    }
    //method: walkForward
    //purpose: moves the camera foward relative to its current rotation(yaw)
    public void walkForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: walkBackward
    //purpose: moves the camera backward relative to its current rotation(yaw)
    public void walkBackwards(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }   
    //method: strafeLeft
    //purpose: strafes the camera left relative to its current rotation(yaw)
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: strafeRight
    //purpose: strafes the camera right relative to its current rotation(yaw)
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: moveUp
    //purpose: moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance){
        position.y -= distance;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: moveDown
    //purpose: moves the camera down relative to its current rotation (yaw)
    public void moveDown(float distance){
        position.y += distance;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: moveLight
    //purpose: moves the light source in accordance with the predetermined path to simulate Day/Night cycle
    public void moveLight()
    {
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if(goingUp)
        {
            yOffset = 1.0f;
        }
        else
        {
            yOffset = -1.0f;
        }
        if(goingRight)
        {
            xOffset = 1.0f;
        }
        else
        {
            xOffset = -1.0f;
        }
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x+=xOffset).put(lPosition.y+=yOffset).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: lookThrough
    //purpose: translate and rotate the matrix so that it looks through the camera
    //this does basicall what gluLookAt() does
    public void lookThrough(){
        //rotate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
        FloatBuffer lightPosition= BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: gameLoop
    //purpose: Maintains the game flowing continuing as long as the game
    //has not been closed, and allows player to move the camera using the 
    //keyboard and mouse
    public void gameLoop() throws InterruptedException{
        FPCameraController camera = new FPCameraController(0,0,0);
        float dx = 0.0f;
        float dy = 0.0f;
        //length of frame
        float dt = 0.0f; 
        //when the last frame was
        float lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        float jumpSpeed = 0.6f;
        //hide the mouse
        Mouse.setGrabbed(true);
        
        //keep looping till the display window is closed the ESC key is down
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;
            //control camera yaw from x movement from the mouse
            camera.yaw(dx * mouseSensitivity);
            //control camera pitch from y movement from the mouse
            camera.pitch(dy * mouseSensitivity);
            //distance in mouse movement from the last getDX() call
            dx = Mouse.getDX();
            //distance in mouse movement from the last getDY() call
            dy = Mouse.getDY();           
            
            /*when passing in the distance to move
                we times the movementSpeed with dt this is a time sacle
                so if its a slow frame u move more than a fast frame
                so on a slow computer you move just as fast as on a fast computer
            */
            if (Keyboard.isKeyDown(Keyboard.KEY_W)){
                //move forward
                camera.walkForward(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                //move backwards
                camera.walkBackwards(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                //strafe left
                camera.strafeLeft(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                //strafe right
                camera.strafeRight(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                //move up
                camera.moveUp(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                //move down
                camera.moveDown(movementSpeed);
            }
            //Move the light source
            camera.moveLight();
            //Keeps the light source moving from x=-20 to x=220 in a cycle.
            if(camera.lPosition.x <= -20.0f)
            {
                camera.goingRight = true;
            }
            else if(camera.lPosition.x >= 220.0f)
            {
                camera.goingRight = false;
            }
            //Keeps the light source moving from y=-20 to y=220 in a cycle.
            if(camera.lPosition.y <= -20.0f)
            {
                camera.goingUp = true;
            }
            else if(camera.lPosition.y >= 220.0f)
            {
                camera.goingUp = false;
            }
            //Swaps the textures, if you're into that sort of thing.
            if(Keyboard.isKeyDown(Keyboard.KEY_Q))
            {
                chunk.cubeSwap(0, 0, 0);
                Thread.sleep(150);
            }
            
           //set the modelview matrix back to the identity
           glLoadIdentity();
           //look through the camera before you draw anything
           camera.lookThrough();
           glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
           //you would draw your scene here
           //render();
           render();
           //draw the buffer to the screen
           Display.update();
           Display.sync(60);
        }
        Display.destroy();
    }
    
    //method: render
    //purpose: Creates the cube using 6 squares with different colors, width
    //of size 2, and lines between the edges of the cube
    private void render() {
        if (firstRendIteration) {
            firstRendIteration = false;
            chunk = new Chunks(0, 0, 0);
        }
        glEnable(GL_DEPTH_TEST);
        chunk.render();
    }
}
