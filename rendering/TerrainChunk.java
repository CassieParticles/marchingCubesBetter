package rendering;

import gameLogic.Camera;
import gameLogic.Generator;
import org.joml.Vector3f;

public class TerrainChunk {
    private final Vector3f offset;
    private final Generator generator;

    private float[][][] scalarField;
    private Mesh mesh;

    public TerrainChunk(Vector3f offset, Mesh mesh,Generator generator){
        this.offset=offset;
        this.generator=generator;
        this.mesh=mesh;
    }

    public TerrainChunk(Vector3f offset, float[][][] scalarField, Generator generator){
        this.offset=offset;
        this.scalarField=scalarField;
        this.generator=generator;
        reCalcMesh();
    }

    public void setScalars(float[][][] scalarField){
        this.scalarField=scalarField;
    }

    public float[][][] getScalars(){
        return scalarField;
    }

    public void reCalcMesh(){
        if(mesh!=null){
            mesh.cleanup();
        }
        mesh=generator.calcMesh(scalarField);
    }

    public void changeOffset(Vector3f newOffset){
        offset.set(newOffset);
    }

    public void render(Program program, Camera camera){
        program.setUniform("translation",offset);
        mesh.render(program, camera);
    }

    public void cleanup(){
        mesh.cleanup();
    }
}
