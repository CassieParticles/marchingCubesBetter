package gameLogic;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends Controller{
    private Matrix4f projectionMatrix;

    public Camera(float moveSpeed, float rotateSpeed) {
        super(moveSpeed, rotateSpeed);
    }

    public void calculateProjectionMatrix(float FOV, float zNear, float zFar, float aspectRatio){
        this.projectionMatrix=new Matrix4f().perspective(FOV,aspectRatio, zNear, zFar);
    }

    public Matrix4f getViewMatrix(){
        return new Matrix4f().identity().
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).translate(new Vector3f(position).mul(-1));
    }

    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }
}
