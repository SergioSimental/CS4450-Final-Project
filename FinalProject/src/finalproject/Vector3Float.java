/***************************************************************
* file: Vector3Float.java
* authors: Val Feist, Eric Platt, Sergio Simental
* class: CS 4450 Computer Graphics
*
* assignment: Final Project Final Checkpoint
* date last modified: 4/23/2020
*
* purpose: Vector3Float stores the position of the camera
*
****************************************************************/

package finalproject;

public class Vector3Float {

    public float x, y, z;
    
    //method: Vector3Float
    //purpose: Constructor holding the position of the camera's x, y, and z
    //coordinates
    public Vector3Float(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
