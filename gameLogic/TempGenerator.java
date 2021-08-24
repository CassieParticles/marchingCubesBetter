package gameLogic;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import rendering.Mesh;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TempGenerator {


    private TriangulationTable triangulationTable;
    private PerlinNoise perlinNoise;

    private float frequency=0.84f;

    public TempGenerator(){
        this.triangulationTable=new TriangulationTable();
        this.perlinNoise=new PerlinNoise(10,0.1f);
    }

    private float normalise(float a, float b, float r){
        if(b==a){return 0;}
        return (r-a)/(b-a);
    }

    private float square(float a){
        return a*a;
    }

    private Vector3f interpolateEdge(Vector3f cornerA, Vector3f cornerB, float valA, float valB, float isoLevel){
        if(cornerA.x!=cornerB.x){
            if(cornerA.x<cornerB.x){
                return new Vector3f(cornerA.x+normalise(valA,valB,isoLevel),cornerA.y,cornerA.z);
            }else{
                return new Vector3f(cornerA.x-normalise(valA,valB,isoLevel),cornerA.y,cornerA.z);
            }
        }else if(cornerA.y!=cornerB.y){
            if(cornerA.y<cornerB.y){
                return new Vector3f(cornerA.x,cornerA.y+normalise(valA,valB,isoLevel),cornerA.z);
            }else{
                return new Vector3f(cornerA.x,cornerA.y-normalise(valA,valB,isoLevel),cornerA.z);
            }

        }else if(cornerA.z!=cornerB.z){
            if(cornerA.z<cornerB.z){
                return new Vector3f(cornerA.x,cornerA.y,cornerA.z+normalise(valA,valB,isoLevel));
            }else{
                return new Vector3f(cornerA.x,cornerA.y,cornerA.z-normalise(valA,valB,isoLevel));
            }
        }
        return null;
    }

    public float[][][] scalarGenerator(int width, int height, int depth, int radius, int noiseMagnitude){
        Random rand= ThreadLocalRandom.current();
        float[][][] field=new float[width][height][depth];
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                for(int z=0;z<height;z++){
                    float xCentre=x-(float)(width-1)/2;
                    float yCentre=y-(float)(height-1)/2;
                    float zCentre=z-(float)(depth-1)/2;
                    float val=(float)Math.sqrt(xCentre*xCentre+yCentre*yCentre+zCentre*zCentre);

                    field[x][y][z]= (radius-val)+(perlinNoise.genPoint(x*frequency,y*frequency,z*frequency)-0.95f)*noiseMagnitude;
                }
            }
        }
        return field;
    }

    public Mesh generate(float[][][] scalarField,float isoLevel){
        int width=scalarField.length-1;
        int height=scalarField[0].length-1;
        int depth=scalarField[0][0].length-1;
        float[] triangles=new float[45*width*height*depth];
        int triangleCount=0;
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                for(int z=0;z<depth;z++){

                    int invocId=(x*(width-1)+y)*(height-1)+z;

                    float[] values=new float[]{
                            scalarField[x][y][z],
                            scalarField[x+1][y][z],
                            scalarField[x+1][y][z+1],
                            scalarField[x][y][z+1],
                            scalarField[x][y+1][z],
                            scalarField[x+1][y+1][z],
                            scalarField[x+1][y+1][z+1],
                            scalarField[x][y+1][z+1]};

                    int voxelIndex=0;
                    if(values[0]>=isoLevel){voxelIndex+=1;}
                    if(values[1]>=isoLevel){voxelIndex+=2;}
                    if(values[2]>=isoLevel){voxelIndex+=4;}
                    if(values[3]>=isoLevel){voxelIndex+=8;}
                    if(values[4]>=isoLevel){voxelIndex+=16;}
                    if(values[5]>=isoLevel){voxelIndex+=32;}
                    if(values[6]>=isoLevel){voxelIndex+=64;}
                    if(values[7]>=isoLevel){voxelIndex+=128;}

                    int[] indices=triangulationTable.getTriangle(voxelIndex);
                    int edgeIndex=triangulationTable.getEdges(voxelIndex);

                    Vector3f[] edges=new Vector3f[12];
                    if((edgeIndex&1)==1){
                        edges[0]=interpolateEdge(new Vector3f(x,y,z),new Vector3f(x+1,y,z),values[0],values[1],isoLevel);//x
                    }if((edgeIndex&2)==2){
                        edges[1]=interpolateEdge(new Vector3f(x+1,y,z),new Vector3f(x+1,y,z+1),values[1],values[2],isoLevel);//z
                    }if((edgeIndex&4)==4){
                        edges[2]=interpolateEdge(new Vector3f(x+1,y,z+1),new Vector3f(x,y,z+1),values[2],values[3],isoLevel);//x
                    }if((edgeIndex&8)==8){
                        edges[3]=interpolateEdge(new Vector3f(x,y,z+1),new Vector3f(x,y,z),values[3],values[0],isoLevel);//z
                    }if((edgeIndex&16)==16){
                        edges[4]=interpolateEdge(new Vector3f(x,y+1,z),new Vector3f(x+1,y+1,z),values[4],values[5],isoLevel);//x
                    }if((edgeIndex&32)==32){
                        edges[5]=interpolateEdge(new Vector3f(x+1,y+1,z),new Vector3f(x+1,y+1,z+1),values[5],values[6],isoLevel);//z
                    }if((edgeIndex&64)==64){
                        edges[6]=interpolateEdge(new Vector3f(x+1,y+1,z+1),new Vector3f(x,y+1,z+1),values[6],values[7],isoLevel);//x
                    }if((edgeIndex&128)==128){
                        edges[7]=interpolateEdge(new Vector3f(x,y+1,z+1),new Vector3f(x,y+1,z),values[7],values[4],isoLevel);//z
                    }if((edgeIndex&256)==256){
                        edges[8]=interpolateEdge(new Vector3f(x,y,z),new Vector3f(x,y+1,z),values[0],values[4],isoLevel);//y
                    }if((edgeIndex&512)==512){
                        edges[9]=interpolateEdge(new Vector3f(x+1,y,z),new Vector3f(x+1,y+1,z),values[1],values[5],isoLevel);//y
                    }if((edgeIndex&1024)==1024){
                        edges[10]=interpolateEdge(new Vector3f(x+1,y,z+1),new Vector3f(x+1,y+1,z+1),values[2],values[6],isoLevel);//y
                    }if((edgeIndex&2048)==2048){
                        edges[11]=interpolateEdge(new Vector3f(x,y,z+1),new Vector3f(x,y+1,z+1),values[3],values[7],isoLevel);//y
                    }

                    for(int i=0;indices[i]!=-1;i+=3){
                        Vector3f vertexA=edges[indices[i]];
                        Vector3f vertexB=edges[indices[i+1]];
                        Vector3f vertexC=edges[indices[i+2]];

                        triangles[triangleCount]=vertexA.x;
                        triangles[triangleCount+1]=vertexA.y;
                        triangles[triangleCount+2]=vertexA.z;

                        triangles[triangleCount+3]=vertexB.x;
                        triangles[triangleCount+4]=vertexB.y;
                        triangles[triangleCount+5]=vertexB.z;

                        triangles[triangleCount+6]=vertexC.x;
                        triangles[triangleCount+7]=vertexC.y;
                        triangles[triangleCount+8]=vertexC.z;

                        triangleCount+=9;
                    }
                }
            }
        }

        FloatBuffer verticesBuffer= MemoryUtil.memAllocFloat(triangleCount);
        verticesBuffer.put(Arrays.copyOf(triangles,triangleCount)).flip();

        int vertexBuffer= GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER,vertexBuffer);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER,verticesBuffer,GL46.GL_STATIC_DRAW);

        Mesh mesh=new Mesh(vertexBuffer, triangleCount);

        MemoryUtil.memFree(verticesBuffer);

        return mesh;
    }

}
