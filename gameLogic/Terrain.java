package gameLogic;

import org.joml.Vector3f;
import org.joml.Vector3i;
import rendering.TerrainChunk;

public class Terrain {
    private float[][][] scalarField;
    private TerrainChunk[] chunks;
    private Generator generator;
    private int size;
    private int numberOfChunks;

    private int radius=120;
    private float noiseMagnitude=0.25f;
    private float noiseFrequency=0.31f;
    private int chunkSize=20;

    public Terrain(){
        this.generator=new Generator();
    }

    public void generate(){
        size=(int)(radius*2*(1+noiseMagnitude));
        numberOfChunks=(int)Math.ceil((float)size/chunkSize);
        scalarField=generator.genScalarField(numberOfChunks*chunkSize,radius,noiseFrequency,noiseMagnitude);
        chunks=generator.generateTerrainChunks(scalarField,chunkSize);
    }

    public void setFloat(int x, int y, int z, float val){
        if(x>0&&x<size){
            if(y>0&&y<size){
                if(z>0&&z<size){
                    scalarField[x][y][z]=val;
                }
            }
        }
    }

    public void addFloat(int x, int y, int z, float add){
        if(x>0&&x<size){
            if(y>0&&y<size){
                if(z>0&&z<size){
                    scalarField[x][y][z]+=add;
                }
            }
        }
    }

    public void reCalcMesh(int id){
        if(id==-1){
            return;
        }
        chunks[id].calcMesh(scalarField);
    }

    public int findChunkIdFromCoord(Vector3f coordinate){
        Vector3i intCoord=new Vector3i((int)coordinate.x/chunkSize,(int)coordinate.y/chunkSize,(int)coordinate.z/chunkSize);
        int coord=(intCoord.x*numberOfChunks+intCoord.y)*numberOfChunks+intCoord.z;
        System.out.println(coord);
        if(coord<chunks.length&&coord>0){
            return coord;
        }else{
            return -1;
        }
    }

    public TerrainChunk[] getChunks() {
        return chunks;
    }

    public float[][][] getScalarField(){
        return scalarField;
    }

    public int getChunkSize(){
        return chunkSize;
    }
}
