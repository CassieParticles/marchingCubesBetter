package gameLogic;

import org.joml.Vector3f;

public class TerrainModifier {
    private Terrain terrain;
    public TerrainModifier(Terrain terrain){
        this.terrain=terrain;
    }

    float square(int a){
        return a*a;
    }

    public void addCircleArea(int x, int y, int z, int r,int chunkSize, float opacity){
        for(int localX=-r;localX<=r;localX++){
            for(int localY=-r;localY<=r;localY++){
                for(int localZ=-r;localZ<=r;localZ++){
                    float distSqr=square(localX)+square(localY)+square(localZ);
                    float dist=distSqr<0?0:(float)Math.sqrt(distSqr);
                    float paint=Math.max(r-dist,0)*opacity;
                    System.out.println(paint);
                    terrain.addFloat(localX+x,localY+y,localZ+z,paint);
                }
            }
        }
        for(int chunkX=-chunkSize;chunkX<=chunkSize;chunkX+=chunkSize){
            for(int chunkY=-chunkSize;chunkY<=chunkSize;chunkY+=chunkSize){
                for(int chunkZ=-chunkSize;chunkZ<=chunkSize;chunkZ+=chunkSize){
                    terrain.reCalcMesh(terrain.findChunkIdFromCoord(new Vector3f(chunkX+x,chunkY+y,chunkZ+z))); //Not calculating all relevant chunks
                }
            }
        }
    }
}
