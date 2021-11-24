package gameLogic;

import org.joml.Vector3f;
import org.joml.Vector3i;

import rendering.TerrainChunk;

import java.util.concurrent.ThreadLocalRandom;

public class Terrain {
    private float[][][] scalarField;
    private float[] linearScalarField;
    private TerrainChunk[] chunks;
    private Generator generator;
    private ComputeGeneration comGen;
    private int size;
    private int numberOfChunks;

    private int radius=75;
    private float noiseMagnitude=30;
    private float noiseFrequency=0.17f;
    private int chunkSize=40;

    public Terrain(){
        this.generator=new Generator();
    }

    public void generate() throws Exception {
        numberOfChunks=(int)Math.ceil((radius*2+noiseMagnitude)/chunkSize);
        size=numberOfChunks*chunkSize;
        comGen=new ComputeGeneration(size+1);
        linearScalarField= comGen.generate(radius, ThreadLocalRandom.current().nextFloat(),1f/size,noiseMagnitude );
        chunks=generator.generateTerrainChunksLinear(linearScalarField,size,chunkSize);
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
                    linearScalarField[(x*(size+1)+y)*(size+1)+z]+=add;
                }
            }
        }
    }

    public void reCalcMesh(int id){
        if(id==-1){
            return;
        }
        chunks[id].calcLinearMesh(linearScalarField,size);
    }

    public int findChunkIdFromCoord(Vector3f coordinate){
        Vector3i intCoord=new Vector3i((int)coordinate.x/chunkSize,(int)coordinate.y/chunkSize,(int)coordinate.z/chunkSize);
        int coord=(intCoord.x*numberOfChunks+intCoord.y)*numberOfChunks+intCoord.z;
        if(coord<chunks.length&&coord>=0){
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

    public int getSize(){return size;}

    public void cleanup(){
        comGen.cleanup();
        for(TerrainChunk chunk:chunks){
            chunk.cleanup();
        }
    }
}
