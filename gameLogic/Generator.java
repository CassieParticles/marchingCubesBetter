package gameLogic;

import org.lwjgl.opengl.GL46;
import rendering.Program;
import rendering.Texture3D;

public class Generator {
    private int size;

    private void genChunk(int size,Program scalarGen){  //size refers to the size of the scalar map, not the polygon mesh, the mesh will be 1 unit smaller in all dimensions
        Texture3D scalarPoints=null;
        try{
            scalarPoints=new Texture3D(size,size,size, GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);

            scalarGen.useProgram();
            scalarGen.setUniform("size",size);
            scalarGen.setUniform("scalarTexture",0);

            GL46.glBindImageTexture(0,scalarPoints.getId(),0,false,0,GL46.GL_WRITE_ONLY,GL46.GL_R32F);

            GL46.glDispatchCompute(size,size,size);
            GL46.glMemoryBarrier(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

            GL46.glBindImageTexture(0, 0, 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);

            scalarGen.unlinkProgram();

        }finally{
            if(scalarPoints!=null){
                scalarPoints.cleanup();
            }
        }


    }
}
