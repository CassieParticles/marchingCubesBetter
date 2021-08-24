package rendering;

import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class Texture3D {

    private final int width;
    private final int height;
    private final int depth;

    private final int format;
    private final int dataType;

    private final int id;

    public Texture3D(int width, int height,int depth, int internalFormat, int pixelFormat, int dataType){
        this.id= GL46.glGenTextures();
        this.width=width;
        this.height=height;
        this.depth=depth;
        this.format=pixelFormat;
        this.dataType=dataType;


        GL46.glBindTexture(GL46.GL_TEXTURE_3D,this.id);
        GL46.glTexImage3D(GL46.GL_TEXTURE_3D, 0, internalFormat, this.width, this.height,this.depth, 0, pixelFormat,dataType, (ByteBuffer)null);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);

        GL46.glBindTexture(GL46.GL_TEXTURE_3D,0);
    }

    public int getId(){
        return id;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getDepth(){
        return depth;
    }

    public void cleanup() {
        GL46.glDeleteTextures(id);
    }
}
