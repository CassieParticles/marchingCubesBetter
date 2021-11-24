package gameLogic;

import org.lwjgl.opengl.GL46;
import rendering.Program;
import rendering.Shader;
import utils.FileHandling;

public class ComputeGeneration {
    private final int scalarBufferId;
    private Program scalarComputeProgram;
    private int size;

    public ComputeGeneration(int size) throws Exception{
        this.size=size;
        scalarComputeProgram =new Program();   //Scalar generation
        scalarComputeProgram.attachShaders(new Shader[]{
                new Shader(FileHandling.loadResource("src/shaders/compute/scalarGeneration.glsl"), GL46.GL_COMPUTE_SHADER),
                new Shader(FileHandling.loadResource("src/shaders/compute/noiseGeneration.glsl"), GL46.GL_COMPUTE_SHADER)
        });
        scalarComputeProgram.link();
        scalarComputeProgram.createUniform("radius");
        scalarComputeProgram.createUniform("seed");
        scalarComputeProgram.createUniform("noiseFrequency");
        scalarComputeProgram.createUniform("noiseMagnitude");
        scalarBufferId =GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, scalarBufferId);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER,size*size*size*4,GL46.GL_STATIC_DRAW);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER,0);
    }

    public float[] generateScalar(float radius, float seed, float noiseFrequency, float noiseMagnitude){
        float[] data = new float[size * size * size];
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 0, scalarBufferId);
        scalarComputeProgram.useProgram();

        scalarComputeProgram.setUniform("radius", radius);
        scalarComputeProgram.setUniform("seed", seed);
        scalarComputeProgram.setUniform("noiseFrequency", noiseFrequency);
        scalarComputeProgram.setUniform("noiseMagnitude", noiseMagnitude);

        GL46.glDispatchCompute(size, size, size);
        GL46.glMemoryBarrier(GL46.GL_SHADER_STORAGE_BARRIER_BIT);
        GL46.glGetBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, 0, data);
        scalarComputeProgram.unlinkProgram();
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 0, 0);
        return data;
    }

    public void cleanup(){
        GL46.glDeleteBuffers(scalarBufferId);
        scalarComputeProgram.cleanup();
    }
}
