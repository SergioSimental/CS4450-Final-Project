/***************************************************************
* file: Chunks.java
* authors: Val Feist, Eric Platt, Sergio Simental
* class: CS 4450 Computer Graphics
*
* assignment: Final Project Final Checkpoint
* date last modified: 4/24/2020
*
* purpose: Implements chunking by drawing a series of cubes in an efficient manner
*
****************************************************************/
package finalproject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunks {
    
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    
    
    
    private Block[][][] Blocks;
    private int[][] columnHeights;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;
    private Random r;
    private Random random = new Random();

    
    // method: render
    // purpose: setups the various opengl settings to draw the cubes
    void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE* 24);
        glPopMatrix();
    }
    
    
    // method: rebuildMesh
    // purpose: major function that is responsible for setting up the
    // terain and topology
    // Also setsup the water and surface type
    public void rebuildMesh(float startX, float startY, float startZ) {
        
        // added in checkpoint 2
        float persistence = 0.1f;
        int seed = (int)System.currentTimeMillis();
        
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistence, seed);
        
        VBOColorHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        double localRand = new Random().nextDouble();
        for (int x = 0; x < CHUNK_SIZE; x += 1) {
            for (int z = 0; z < CHUNK_SIZE; z += 1) {
                // there seems to be an error on this line that can cause void
                // at low startY hence the extra addition
                columnHeights[x][z] = (int) ((CHUNK_SIZE*noise.getNoise((int)x, (int)z)) + startY + 7);
                
                
                // Unique Feature: Lakes
                // The idea is to set a barrier for creating water
                // This will convert the lowest topmost layer into water
                if (columnHeights[x][z] < 6) {
                    columnHeights[x][z] = 4;
                }
                for(int y = 0; y <= columnHeights[x][z] && y < CHUNK_SIZE; y++){
                    
                    // this is a messy addition that changes the top layer
                    if (y == columnHeights[x][z]) {
                        // This is the water layer
                        if (columnHeights[x][z] == 4) {
                            Blocks[x][y][z].SetWater();
                        // Else ground of some sort
                        } else if (columnHeights[x][z] == 6) {
                            Blocks[x][y][z].SetSand();
                        } else {
                            Blocks[x][y][z].SetGrass();

                        }
                    }
                    VertexPositionData.put(
                        createCube((float) (startX + x * CUBE_LENGTH),
                        (float)(y*CUBE_LENGTH + (int)(CHUNK_SIZE*.8)),
                        (float) (startZ + z *CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[x][y][z])));
                    VertexTextureData.put(createTexCube((float)0, (float)0,Blocks[x][y][z]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: cubeSwap
    //purpose: Makes the cubes swap textures like Bedrock <-> Grass, Sand <-> Water, and Dirt <-> Stone.
    public void cubeSwap(float startX, float startY, float startZ)
    {
        VBOColorHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        for (int x = 0; x < CHUNK_SIZE; x += 1) {
            for (int z = 0; z < CHUNK_SIZE; z += 1) {
                for(int y = 0; y < CHUNK_SIZE && y <= columnHeights[x][z]; y++){
                    if (Blocks[x][y][z].GetID() == 0)
                    {
                        Blocks[x][y][z].SetBedrock();
                    }
                    else if (Blocks[x][y][z].GetID() == 1)
                    {
                        Blocks[x][y][z].SetWater();
                    }
                    else if (Blocks[x][y][z].GetID() == 2)
                    {
                        Blocks[x][y][z].SetSand();
                    }
                    else if (Blocks[x][y][z].GetID() == 3)
                    {
                        Blocks[x][y][z].SetStone();
                    }
                    else if (Blocks[x][y][z].GetID() == 4)
                    {
                        Blocks[x][y][z].SetDirt();
                    }
                    else if (Blocks[x][y][z].GetID() == 5)
                    {
                        Blocks[x][y][z].SetGrass();
                    }
                    VertexPositionData.put(
                        createCube((float) (startX + x * CUBE_LENGTH),
                        (float)(y*CUBE_LENGTH + (int)(CHUNK_SIZE*.8)),
                        (float) (startZ + z *CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[x][y][z])));
                    VertexTextureData.put(createTexCube((float)0, (float)0,Blocks[x][y][z]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    // method: createCubeVertexCol
    // purpose: this method sets up the colors
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
                CubeColorArray.length];
        }
        return cubeColors;
    }
    
    // method: createCube
    // purpose: to draw into existence a set of cubes
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }
    
    //method: getCubeColor
    //purpose: functionality reduced due to textures
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }
    
    
    //method: Chucks
    //purpose: Constructor
    public Chunks(int startX, int startY, int startZ) {
        try{texture = TextureLoader.getTexture("PNG",
            ResourceLoader.getResourceAsStream("terrain.png"));
        } catch(Exception e){
            System.out.print("ER-ROAR!: Check your terrain");
            e.printStackTrace();
        }  
        r= new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        columnHeights = new int[CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    float localRand = r.nextFloat();
                    if(y > 4){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);     
                    } else if (y == 4) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else if(y == 3 || (y == 2 && localRand >= 0.6f)){
                         Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else if(y == 1 || (y == 2 && localRand < 0.6f)){ 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }else if(y == 0){ 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
      
    // method: createTexCube
    // purpose: To assign the textures to the brick type
    // explanation of cases:
    // case 0 grass
    // case 1 sand
    // case 2 water
    // case 3 dirt
    // case 4 stone
    // default bedrock for now
    
    
    //method: createTexCube
    //purpose: accepts a location in 2D, a block, and setsup the textures
    public static float[] createTexCube(float x, float y, Block block){
        float offset = (1024f/16)/1024f;
        switch(block.GetID()){
            case 0:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    // TOP!
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1};
                
            case 1:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2};
            case 2:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 16, y + offset * 13,
                    // TOP!
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 16, y + offset * 13,
                    // FRONT QUAD
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // BACK QUAD
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // LEFT QUAD
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // RIGHT QUAD
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13};
            case 3:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                   x + offset * 1, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // BACK QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0};
            case 4:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // TOP!
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // BACK QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1};
            default:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                     x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // TOP!
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BACK QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2};
        }
    }
}
