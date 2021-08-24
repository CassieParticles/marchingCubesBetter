package rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Texture2D {

    private final int width;
    private final int height;

    private final int format;
    private final int dataType;

    private final int id;

    public Texture2D(String path) throws Exception {
        int[] array=loadTexture(path);
        this.id=array[0];
        width=array[1];
        height=array[2];
        this.dataType=GL46.GL_UNSIGNED_BYTE;
        this.format=GL46.GL_RGBA;
    }

    public Texture2D(int width, int height, int internalFormat, int pixelFormat, int dataType){
        this.id= GL46.glGenTextures();
        this.width=width;
        this.height=height;
        this.format=pixelFormat;
        this.dataType=dataType;


        GL46.glBindTexture(GL46.GL_TEXTURE_2D,this.id);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, internalFormat, this.width, this.height, 0, pixelFormat,dataType, (ByteBuffer)null);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);

        GL46.glBindTexture(GL46.GL_TEXTURE_2D,0);
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

    private static int[] loadTexture(String fileName) throws Exception {

        int width;
        int height;
        ByteBuffer buf;
        // Load Texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = STBImage.stbi_load(fileName, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + fileName  + "] not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // Create a new OpenGL texture
        int textureId = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, textureId);

        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, width, height, 0,
                GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buf);

        GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D,0);

        STBImage.stbi_image_free(buf);

        return new int[] {textureId,width,height};
    }


    public void writeToTexture(int x, int y,int width, int height,  float[] floats) {

        GL46.glBindTexture(GL46.GL_TEXTURE_2D,id);

        FloatBuffer buffer= BufferUtils.createFloatBuffer(floats.length);

        buffer.put(floats).flip();

        GL46.glTexSubImage2D(GL46.GL_TEXTURE_2D,0,x,y,width,height,format,dataType,buffer);

        GL46.glBindTexture(GL46.GL_TEXTURE_2D,0);

        MemoryUtil.memFree(buffer);
    }


    public void cleanup() {
        GL46.glDeleteTextures(id);
    }
}
