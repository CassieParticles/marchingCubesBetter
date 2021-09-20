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

    public void addCircleArea(int x, int y, int z, int r, float opacity){
        int chunkSize=terrain.getChunkSize();
        for(int localX=-r;localX<=r;localX++){
            for(int localY=-r;localY<=r;localY++){
                for(int localZ=-r;localZ<=r;localZ++){
                    float distSqr=square(localX)+square(localY)+square(localZ);
                    float dist=distSqr<0?0:(float)Math.sqrt(distSqr);
                    float paint=Math.max(r-dist,0)*opacity;
                    terrain.addFloat(localX+x,localY+y,localZ+z,paint);
                }
            }
        }
        int count=0;
        for(int chunkX=x-r;chunkX<=x+r+chunkSize;chunkX+=chunkSize){
            for(int chunkY=y-r;chunkY<=y+r+chunkSize;chunkY+=chunkSize){
                for(int chunkZ=z-r;chunkZ<=z+r+chunkSize;chunkZ+=chunkSize){
                    terrain.reCalcMesh(terrain.findChunkIdFromCoord(new Vector3f(chunkX,chunkY,chunkZ)));
                    count++;
                }
            }
        }
    }
}
