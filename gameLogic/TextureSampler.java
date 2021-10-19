package gameLogic;

import main.Window;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import rendering.*;
import utils.FileHandling;

public class TextureSampler {
    private final FrameBuffer frameBuffer;
    private final Screen tempScreen;

    private final Program screenProgram;

    public TextureSampler(Window window) throws Exception {
        frameBuffer=new FrameBuffer();
        tempScreen=new Screen();
        screenProgram=new Program();

        frameBuffer.attachTextures(new Texture2D[]{
                new Texture2D(window.getWidth(),window.getHeight(), GL46.GL_RGBA,GL46.GL_RGBA,GL46.GL_FLOAT)
        }, new int[]{
                GL46.GL_COLOR_ATTACHMENT0
        });

        screenProgram.attachShaders(new Shader[]{
                new Shader(FileHandling.loadResource("src/shaders/screen/vertex.glsl"),GL46.GL_VERTEX_SHADER),
                new Shader(FileHandling.loadResource("src/shaders/screen/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
        });

        screenProgram.link();
    }

    public float[] sampleTexture(int x, int y, int width, int height, Texture2D texture){
        frameBuffer.bindFrameBuffer();
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT |GL46.GL_COLOR_BUFFER_BIT);

        screenProgram.useProgram();
        tempScreen.render(screenProgram,texture);

        screenProgram.unlinkProgram();

        GL46.glBindTexture(GL46.GL_TEXTURE_2D,texture.getId());
        float[] floatArray=new float[width*height*4];

        GL46.glReadPixels(x,y,width,height,GL46.GL_RGB,GL46.GL_FLOAT,floatArray);

        GL46.glBindTexture(GL46.GL_TEXTURE_2D,0);
        frameBuffer.unbindFrameBuffer();

        return floatArray;
    }

    public void cleanup(){
        frameBuffer.cleanup();
        tempScreen.cleanup();
        screenProgram.cleanup();
    }
}
