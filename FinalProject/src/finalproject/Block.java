/***************************************************************
* file: Block.java
* authors: Val Feist, Eric Platt, Sergio Simental
* class: CS 4450 Computer Graphics
*
* assignment: Final Project Final Checkpoint
* date last modified: 4/23/2020
*
* purpose: Sets up the block class
*
****************************************************************/
package finalproject;


public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    public enum BlockType{
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);
        
        private int BlockID;
        
        BlockType(int i){
            BlockID = i;
        }
        public int GetID(){
            return BlockID;
        }
        public void SetID(int i){
            BlockID = i;
        }
    }
    
    // method: Block
    // purpose: Constructor
    // Accepts an enum that is used to identify its type which is used for
    // terain at this point
    public Block(BlockType type){
        Type = type;
    }
    
    
    // method: setCoords
    // purpose: sets the location of the object in 3D space
    public void setCoords(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    // method: IsActive
    // purpose: getter that returns the status
    public boolean IsActive(){
            return IsActive;
    }
    
    
    // method: SetActive
    // purpose: a setter for IsActive member
    public void SetActive(boolean active){
        IsActive = active;
    }
    
    // method: GetID
    // purpose: a getter that shows the numerical value of the class's enum
    // currently used for textures
    public int GetID(){
        return Type.GetID();
    }
    
    
    // method: SetWater()
    // purpose: We needed a way to change the top layer of the topology
    public void SetWater() {
        Type = Block.BlockType.BlockType_Water;
    }
    
    // method: SetWater()
    // purpose: We needed a way to change the top layer of the topology
    public void SetSand() {
        Type = Block.BlockType.BlockType_Sand;
    }
    
    // method: SetGrass()
    // purpose: We needed a way to change the top layer of the topology
    public void SetGrass() {
        Type = Block.BlockType.BlockType_Grass;
    }
    // method: SetBedrock()
    // purpose: We needed a way to change the top layer of the topology
    public void SetBedrock() {
        Type = Block.BlockType.BlockType_Bedrock;
    }
    
    // method: SetStone()
    // purpose: We needed a way to change the top layer of the topology
    public void SetStone() {
        Type = Block.BlockType.BlockType_Stone;
    }
    
    // method: SetDirt()
    // purpose: We needed a way to change the top layer of the topology
    public void SetDirt() {
        Type = Block.BlockType.BlockType_Dirt;
    }
}
